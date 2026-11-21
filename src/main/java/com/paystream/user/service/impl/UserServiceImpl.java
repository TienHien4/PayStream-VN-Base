package com.paystream.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paystream.common.exception.BusinessException;
import com.paystream.common.exception.NotFoundException;
import com.paystream.user.dto.*;
import com.paystream.user.entity.Role;
import com.paystream.user.entity.User;
import com.paystream.user.event.Event;
import com.paystream.user.event.EventFactory;
import com.paystream.user.event.UserCreatedEvent;
import com.paystream.user.mapper.UserMapper;
import com.paystream.user.repository.RoleRepository;
import com.paystream.user.repository.UserRepository;
import com.paystream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public UserProfileResponse createUser(UserCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("USR-409-01", "Email này đã được đăng ký trong hệ thống.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        user.setPhone(request.getPhone());
        user.setMerchantId(request.getMerchantId());
        user.setStatus("ACTIVE");

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findByIdIn(request.getRoleIds());
            user.setRoles(new HashSet<>(roles));
        }

        user = userRepository.save(user);

        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .merchantId(user.getMerchantId())
                .build();

        // Publish to Kafka (simplified)
        try {
            Event<?> event = EventFactory.INSTANCE.createEvent(
                    "USER_CREATED",
                    UUID.randomUUID().toString(),
                    userCreatedEvent
                    );
            String evenJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("notification.events", UUID.randomUUID().toString(), evenJson);
        } catch (Exception e) {
            throw new BusinessException("USR-500-01", "Failed to publish user creation event to Kafka: " + e.getMessage());
            // Ignored in local dev if kafka is not reachable
        }

        return userMapper.toProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userProfile", key = "#userId")
    public UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USR-404-01", "User not found"));
        return userMapper.toProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getUsersByMerchant(String merchantId) {
        return userRepository.findByMerchantId(merchantId).stream()
                .map(userMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<UserProfileResponse> addUsersToMerchant(String merchantId, AddUsersToMerchantRequest request) {
        List<User> users = userRepository.findAllById(request.getUserIds());
        if (users.isEmpty()) {
            throw new NotFoundException("USR-404-02", "Không tìm thấy người dùng nào trong danh sách được cung cấp.");
        }

        for (User user : users) {
            user.setMerchantId(merchantId);
        }

        users = userRepository.saveAll(users);

        // Evict cache for all updated users
        if (cacheManager != null) {
            var userProfileCache = cacheManager.getCache("userProfile");
            var userPermissionsCache = cacheManager.getCache("userPermissions");
            for (User user : users) {
                if (userProfileCache != null) userProfileCache.evict(user.getId());
                if (userPermissionsCache != null) userPermissionsCache.evict(user.getId());
            }
        }

        return users.stream()
                .map(userMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"userProfile", "userPermissions"}, key = "#userId")
    public UserProfileResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USR-404-01", "User not found"));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            user.setStatus(request.getStatus());
        }
        if (request.getRoleIds() != null) {
            List<Role> roles = roleRepository.findByIdIn(request.getRoleIds());
            user.getRoles().clear();
            user.getRoles().addAll(roles);
        }

        user = userRepository.save(user);
        return userMapper.toProfileResponse(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"userProfile", "userPermissions"}, key = "#userId")
    public UserProfileResponse updateUserRoles(String userId, RoleUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USR-404-01", "User not found"));

        List<Role> roles = roleRepository.findByIdIn(request.getRoleIds());
        user.getRoles().clear();
        user.getRoles().addAll(roles);

        user = userRepository.save(user);
        return userMapper.toProfileResponse(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"userProfile", "userPermissions"}, key = "#userId")
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USR-404-01", "User not found"));
        // Soft delete for audit compliance
        user.setStatus("INACTIVE");
        userRepository.save(user);
    }

    @Override
    public void removeUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USR-404-01", "User not found"));
        // Soft delete for audit compliance
        user.setMerchantId(null);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(r -> new RoleDto(r.getId(), r.getCode(), r.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProfileResponse> searchUser(String fullName, String email) {
        return userRepository.searchUser(fullName, email).stream()
                .map(userMapper::toProfileResponse).collect(Collectors.toList());
    }

    @Override
    public List<UserProfileResponse> searchUserByMerchant(String fullName, String email, String merchantId) {
        return userRepository.searchUserMerchant(fullName, email, merchantId).stream()
                .map(userMapper::toProfileResponse).collect(Collectors.toList());
    }
}

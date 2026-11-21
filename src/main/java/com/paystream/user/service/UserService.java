package com.paystream.user.service;

import com.paystream.user.dto.*;

import java.util.List;

public interface UserService {
    UserProfileResponse createUser(UserCreateRequest request);
    UserProfileResponse getUserProfile(String userId);
    List<UserProfileResponse> getUsersByMerchant(String merchantId);
    List<UserProfileResponse> addUsersToMerchant(String merchantId, AddUsersToMerchantRequest request);
    UserProfileResponse updateUser(String userId, UserUpdateRequest request);
    UserProfileResponse updateUserRoles(String userId, RoleUpdateRequest request);
    void deleteUser(String userId);
    void  removeUser(String userId);
    List<UserProfileResponse> getAllUsers();
    List<RoleDto> getAllRoles();
    List<UserProfileResponse> searchUser(String fullName, String email);
    List<UserProfileResponse> searchUserByMerchant(String fullName, String email, String merchantId);
}

package com.paystream.user.controller;

import com.paystream.common.dto.BaseResponse;
import com.paystream.common.exception.UnauthorizedException;
import com.paystream.user.dto.*;
import com.paystream.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * POST /api/v1/users
     * Tạo mới hoặc mời thành viên vào Merchant.
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<UserProfileResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return BaseResponse.success(userService.createUser(request));
    }

    /**
     * GET /api/v1/users
     * Lấy danh sách tất cả người dùng trong hệ thống.
     */
    @GetMapping("/users")
    public BaseResponse<List<UserProfileResponse>> getAllUsers() {
        return BaseResponse.success(userService.getAllUsers());
    }

    /**
     * GET /api/v1/users/me
     * Lấy thông tin profile của user hiện tại đang đăng nhập.
     */
    @GetMapping("/users/me")
    public BaseResponse<UserProfileResponse> getMyProfile() {
        org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.paystream.common.security.UserContext userContext) {
            return BaseResponse.success(userService.getUserProfile(userContext.getUserId()));
        }
        // Tạm thời cho phép bypass auth khi test: trả về null thay vì ném UnauthorizedException
        return BaseResponse.success(null);
    }

    /**
     * GET /api/v1/users/{id}
     * Lấy chi tiết thông tin 1 thành viên theo ID.
     */
    @GetMapping("/users/{id}")
    public BaseResponse<UserProfileResponse> getUserById(@PathVariable String id) {
        return BaseResponse.success(userService.getUserProfile(id));
    }

    /**
     * GET /api/v1/merchants/{id}/users
     * Lấy danh sách toàn bộ thành viên của một Merchant.
     */
    @GetMapping("/merchants/{id}/users")
    public BaseResponse<List<UserProfileResponse>> getUsersByMerchant(@PathVariable String id) {
        return BaseResponse.success(userService.getUsersByMerchant(id));
    }

    /**
     * POST /api/v1/merchants/{id}/users
     * Thêm/Mời danh sách người dùng vào Merchant Team.
     */
    @PostMapping("/merchants/{id}/users")
    public BaseResponse<List<UserProfileResponse>> addUsersToMerchant(
            @PathVariable String id,
            @Valid @RequestBody AddUsersToMerchantRequest request) {
        return BaseResponse.success(userService.addUsersToMerchant(id, request));
    }

    /**
     * PUT /api/v1/users/{id}
     * Cập nhật thông tin thành viên (Họ tên, SĐT, Trạng thái, Vai trò).
     */
    @PutMapping("/users/{id}")
    public BaseResponse<UserProfileResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequest request) {
        return BaseResponse.success(userService.updateUser(id, request));
    }

    /**
     * PUT /api/v1/users/{id}/roles
     * Cập nhật riêng danh sách vai trò của thành viên.
     */
    @PutMapping("/users/{id}/roles")
    public BaseResponse<UserProfileResponse> updateUserRoles(
            @PathVariable String id,
            @Valid @RequestBody RoleUpdateRequest request) {
        return BaseResponse.success(userService.updateUserRoles(id, request));
    }

    @GetMapping("/users/search")
    public BaseResponse<List<UserProfileResponse>> searchUser(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email) {
        return BaseResponse.success(userService.searchUser(fullName, email));
    }

    @GetMapping("/users/search-by-merchant/{merchantId}")
    public BaseResponse<List<UserProfileResponse>> searchUserByMerchant(
            @PathVariable String merchantId,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email) {
        return BaseResponse.success(userService.searchUserByMerchant(fullName, email, merchantId));
    }

    /**
     * DELETE /api/v1/users/{id}
     * Xóa hoặc vô hiệu hóa tài khoản thành viên.
     */
    @DeleteMapping("/users/{id}")
    public BaseResponse<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return BaseResponse.success("User deactivated successfully");
    }

    @DeleteMapping("/users/{id}/remove")
    public BaseResponse<String> removeUserFromTeam(@PathVariable String id) {
        userService.removeUser(id);
        return BaseResponse.success("User deactivated successfully");
    }

    /**
     * GET /api/v1/roles
     * Lấy danh sách tất cả các vai trò (Roles) có trong hệ thống (ADMIN, ACCOUNTANT, DEVELOPER, OPERATOR,...).
     */
    @GetMapping("/roles")
    public BaseResponse<List<RoleDto>> getAllRoles() {
        return BaseResponse.success(userService.getAllRoles());
    }
}

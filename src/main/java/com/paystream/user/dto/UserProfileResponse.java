package com.paystream.user.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileResponse {
    private String id;
    private String merchantId;
    private String email;
    private String fullName;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    
    // Flat array containing permission codes like ["VIEW_PAYMENT", "REFUND_ORDER"]
    private List<String> permissions;

    // Detailed roles info
    private List<RoleDto> roles;

    // Flat list of role codes like ["ADMIN", "ACCOUNTANT", "DEVELOPER"]
    private List<String> roleCodes;
}

package com.paystream.user.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class UserCreateRequest {

    @JsonProperty("merchant_id")
    @JsonAlias({"merchantId", "merchant_id"})
    private String merchantId;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name is required")
    @JsonProperty("full_name")
    @JsonAlias({"fullName", "full_name"})
    private String fullName;

    @Pattern(regexp = "^(\\+84|0)[3|5|7|8|9][0-9]{8}$", message = "Invalid phone number format")
    private String phone;

    @JsonProperty("role_ids")
    @JsonAlias({"roleIds", "role_ids"})
    private List<String> roleIds;
}

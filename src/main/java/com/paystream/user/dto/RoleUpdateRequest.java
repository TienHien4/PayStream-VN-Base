package com.paystream.user.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RoleUpdateRequest {
    @NotEmpty(message = "Role IDs cannot be empty")
    @JsonProperty("role_ids")
    @JsonAlias({"roleIds", "role_ids"})
    private List<String> roleIds;
}

package com.paystream.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUsersToMerchantRequest {

    @NotEmpty(message = "Danh sách user_ids không được để trống")
    @JsonProperty("user_ids")
    private List<String> userIds;
}

package com.paystream.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();
    private String traceId;

    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .code("200")
                .message("Operation successful")
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> error(String code, String message, String traceId) {
        return BaseResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .traceId(traceId)
                .build();
    }
}

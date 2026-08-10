package com.kv.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnblockRequest {

    @NotBlank
    private String targetType;

    @NotBlank
    private String targetValue;
}

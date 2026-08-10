package com.kv.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlockRequest {

    @NotBlank
    private String targetType;

    @NotBlank
    private String targetValue;

    private String reason;

    private int expireMinutes;
}

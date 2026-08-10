package com.kv.core.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserProfileDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private String role;
    private BigDecimal balance;
    private Boolean allowAiCrawl;
    private Boolean allowPublicIndex;
    private LocalDateTime createdAt;
}

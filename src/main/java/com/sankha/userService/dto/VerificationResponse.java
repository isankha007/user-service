package com.sankha.userService.dto;

import java.util.UUID;

public record VerificationResponse(String status, String role, String username, UUID userId) {
}

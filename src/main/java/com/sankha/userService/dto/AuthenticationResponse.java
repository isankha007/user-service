package com.sankha.userService.dto;

public record AuthenticationResponse(String status, String email, String token) {
}

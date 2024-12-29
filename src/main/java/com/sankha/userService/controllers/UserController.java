package com.sankha.userService.controllers;

import com.sankha.userService.dto.*;
import com.sankha.userService.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	void registerUser(@RequestBody @Valid UserRequest userRequest,
	                  @RequestParam(value = "referredby", required = false) UUID userId) {
		userService.register(userRequest, userId);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(userService.login(loginRequest));
	}

	@PostMapping("/verify")
	public ResponseEntity<VerificationResponse> verifyToken(@RequestBody VerificationRequest token) {
		return ResponseEntity.ok(userService.verifyToken(token));
	}
}

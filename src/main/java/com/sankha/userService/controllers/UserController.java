package com.sankha.userService.controllers;

import com.sankha.userService.dto.UserRequest;
import com.sankha.userService.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
}

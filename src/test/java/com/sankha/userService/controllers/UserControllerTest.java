package com.sankha.userService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankha.userService.dto.LoginRequest;
import com.sankha.userService.dto.UserRequest;
import com.sankha.userService.dto.VerificationRequest;
import com.sankha.userService.entities.Role;
import com.sankha.userService.exceptions.UserAlreadyExistException;
import com.sankha.userService.services.JwtService;
import com.sankha.userService.services.UserService;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private UserService userServiceMock;

	@MockBean
	private JwtService jwtService;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	void userShouldBeAbleToRegisterWithValidDetails() throws Exception {
		UserRequest userRequest =
				new UserRequest("abc@example.com", "9158986369",
						"Email", "password", Role.ADMIN);
		String json = objectMapper.writeValueAsString(userRequest);
		UUID referredByUserId = UUID.randomUUID();
		mockMvc.perform(post("/users/register")
						.param("referredby", String.valueOf(referredByUserId))
						.content(json)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		verify(userServiceMock).register(userRequest, referredByUserId);

	}

	@Test
	void userShouldBeAbleToRegisterWithoutReferral() throws Exception {
		UserRequest userRequest =
				new UserRequest("abc@example.com", "9158986369",
						"Email", "password", Role.ADMIN);
		String json = objectMapper.writeValueAsString(userRequest);
		mockMvc.perform(post("/users/register")
						.content(json)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		verify(userServiceMock).register(userRequest, null);

	}

	@Test
	void shouldThrowExceptionWhenUserAlreadyExist() throws Exception {
		UserRequest userRequest =
				new UserRequest("abc@example.com", "9158986369",
						"Email", "password", Role.ADMIN);
		;
		String json = objectMapper.writeValueAsString(userRequest);
		UUID id = UUID.randomUUID();
		String referredByUserId = id.toString();

		doThrow(new UserAlreadyExistException("User already exists")).when(userServiceMock).register(userRequest,id);
		mockMvc.perform(post("/users/register")
						.param("referredby", String.valueOf(referredByUserId))
						.content(json)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verify(userServiceMock).register(userRequest,id);
	}

	@Test
	void UserShouldBeAbleToLogin() throws Exception {
		LoginRequest loginRequest = new LoginRequest("abc@example.coms", "password");
		String json = objectMapper.writeValueAsString(loginRequest);
		mockMvc.perform(
						post("/users/login")
								.content(json)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk());


		Mockito.verify(userServiceMock).login(loginRequest);
	}

	@Test
	void JwtTokenShouldBeVerifiedSuccessfully() throws Exception {
		String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
				".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ" +
				".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
		String userName = "abc@example.com";
		VerificationRequest verificationRequest = new VerificationRequest(jwt,userName);
		String jsonString = objectMapper.writeValueAsString(verificationRequest);
		mockMvc.perform(
						post("/users/verify")
								.content(jsonString)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk());

		Mockito.verify(userServiceMock).verifyToken(verificationRequest);
	}

	@Test
	void JwtTokenVerificationShouldFail() throws Exception {
		String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
				".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ" +
				".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
		String userName = "abc@example.com";
		VerificationRequest verificationRequest = new VerificationRequest(jwt, userName);
		String jsonString = objectMapper.writeValueAsString(verificationRequest);
		doThrow(SignatureException.class).when(userServiceMock).verifyToken(verificationRequest);
		mockMvc.perform(
						post("/users/verify")
								.content(jsonString)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isUnauthorized());

		Mockito.verify(userServiceMock).verifyToken(verificationRequest);
	}
}
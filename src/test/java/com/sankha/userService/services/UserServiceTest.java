package com.sankha.userService.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankha.userService.builder.UserBuilder;
import com.sankha.userService.dto.UserRequest;
import com.sankha.userService.dto.VerificationRequest;
import com.sankha.userService.dto.VerificationResponse;
import com.sankha.userService.entities.Role;
import com.sankha.userService.entities.User;
import com.sankha.userService.exceptions.UserAlreadyExistException;
import com.sankha.userService.repositories.UserRepository;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserServiceTest {
	ObjectMapper objectMapper;
	@Mock
	private UserRepository userRepository;
	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private AuthenticationManager authenticationManager;


	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;
	private String topic = "user_event";
	@InjectMocks
	private UserService userService;

	@Mock
	private JwtService jwtService;
	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper();
	}
	@Test
	void userShouldBeCreatedWithDetailsProvided() {
		UserRequest userRequest =
				new UserRequest("abc@example.com", "9158986369",
						"Email", "password", Role.ADMIN);
		ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
		User user = objectMapper.convertValue(userRequest, User.class);
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(passwordEncoder.encode(user.getPassword())).thenReturn(anyString());

		userService.register(userRequest, null);

		verify(userRepository).save(argumentCaptor.capture());
		User actualUser = argumentCaptor.getValue();
		Assertions.assertEquals(userRequest.email(), actualUser.getEmail());
		Assertions.assertEquals(userRequest.phoneNumber(), actualUser.getPhoneNumber());
		Assertions.assertEquals(userRequest.phoneNumber(), actualUser.getPhoneNumber());
		Assertions.assertEquals(userRequest.role(), actualUser.getRole());
		Assertions.assertNotNull(actualUser.getPassword());
	}

	@Test
	void shouldThrowExceptionIfAlreadyExist() {
		UserRequest userRequest =
				new UserRequest("abc@example.com", "9158986369",
						"Email", "password", Role.ADMIN);
		User user = objectMapper.convertValue(userRequest, User.class);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

		UserAlreadyExistException userAlreadyExistException = assertThrows(UserAlreadyExistException.class, () -> userService.register(userRequest, null));

		Assertions.assertEquals("User Already exist",userAlreadyExistException.getMessage());
	}

	@Test
	void ShouldInformUserIsCreatedOnSuccessfulRegistration() {
		UserRequest userRequest =
				new UserRequest("abc@example.com", "9158986369",
						"Email", "password", Role.ADMIN);
		User user = objectMapper.convertValue(userRequest, User.class);
		when(userRepository.save(any(User.class))).thenReturn(user);
		ArgumentCaptor<UserRequest> userRequestArgumentCaptor = ArgumentCaptor.forClass(UserRequest.class);

		userService.register(userRequest,null);
		verify(kafkaTemplate).send(anyString(),anyString());
	}

	@Test
	void shouldVerifyToken() throws JsonProcessingException {
		String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
				".eyJzdWIiOiJhYmNAZXhhbXBsZS5jb20iLCJuYW1lIjoiSm9obiBEb2UiLCJpYXQiOjE1MTYyMzkwMjJ9" +
				".gL_-Olf6RhJNiiGnO4aORqmUt8SUJrzlCp6txshQXw8";
		String userName = "abc@example.com";
		User user = new UserBuilder().withEmail(userName).withId(UUID.randomUUID()).withRole(Role.USER).build();
		when(jwtService.validateToken(jwt,user)).thenReturn(true);
		when(userDetailsService.loadUserByUsername(userName)).thenReturn(user);
		when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
		VerificationRequest verificationRequest = new VerificationRequest(jwt, userName);

		VerificationResponse response = userService.verifyToken(verificationRequest);

		Assertions.assertNotNull(response.userId());
		Assertions.assertEquals(userName,response.username());
	}

	@Test
	void shouldNotVerifyToken() throws JsonProcessingException {
		String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
				".eyJzdWIiOiJhYmNAZXhhbXBsZS5jb20iLCJuYW1lIjoiSm9obiBEb2UiLCJpYXQiOjE1MTYyMzkwMjJ9" +
				".gL_-Olf6RhJNiiGnO4aORqmUt8SUJrzlCp6txshQXw8";
		String userName = "abc@example.com";
		User user = new UserBuilder().withEmail(userName).withRole(Role.USER).build();
		when(userDetailsService.loadUserByUsername(userName)).thenReturn(user);
		when(jwtService.validateToken(jwt,user)).thenReturn(false);
		VerificationRequest verificationRequest = new VerificationRequest(jwt, userName);
		String jsonString = objectMapper.writeValueAsString(verificationRequest);

		VerificationResponse response = userService.verifyToken(verificationRequest);

		//Assertions.assertNull(response.token());
		Assertions.assertNull(response.username());
	}
}
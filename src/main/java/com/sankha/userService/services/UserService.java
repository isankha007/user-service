package com.sankha.userService.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankha.userService.config.AppConstants;
import com.sankha.userService.dto.*;
import com.sankha.userService.entities.User;
import com.sankha.userService.exceptions.UserAlreadyExistException;
import com.sankha.userService.repositories.UserRepository;
import com.sankha.userService.subscribers.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sankha.userService.config.AppConstants.CREATE_USER;
import static com.sankha.userService.config.AppConstants.USER_EVENT;

@Service
@RequiredArgsConstructor
public class UserService {
	private final PasswordEncoder passwordEncoder;
	private final UserRepository repository;
	private final UserDetailsService userDetailsService;
	private final JwtService jwtService;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	ObjectMapper mapper = new ObjectMapper();
	//@Value("${user.kafka.topic}")
	private String topic = USER_EVENT;

	public void register(UserRequest userRequest, UUID referredByUserId) {
		User user = extractUserFromRequest(userRequest);
		if (userExist(user)) {
			throw new UserAlreadyExistException(AppConstants.USER_ALREADY_EXIST);
		}
		User saved = repository.save(user);
//		walletService.createWallet(saved.getId());
		if (saved != null) {
			sendMessage(referredByUserId, saved);
		}

	}

	private void sendMessage(UUID referredByUserId, User saved) {
		UserEvent userEvent = new UserEvent(saved.getEmail(), saved.getPhoneNumber(),
				saved.getPreference(), CREATE_USER, referredByUserId, saved.getId());
		try {
			String jsonString = mapper.writeValueAsString(userEvent);
			this.kafkaTemplate.send(this.topic, jsonString);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean userExist(User user) {
		User byMail = repository.findByEmail(user.getEmail()).orElse(null);
		User byNumber = repository.findByPhoneNumber(user.getPhoneNumber()).orElse(null);
		return (byMail != null || byNumber != null);
	}

	private User extractUserFromRequest(UserRequest userRequest) {
		return User.builder().email(userRequest.email()).role(userRequest.role()).phoneNumber(userRequest.phoneNumber())
				.password(passwordEncoder.encode(userRequest.password()))
				.preference(userRequest.preference()).build();
	}

	public AuthenticationResponse login(LoginRequest loginRequest) {
		return null;
	}

	public VerificationResponse verifyToken(VerificationRequest verificationRequest) {
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(verificationRequest.username());
		boolean isTokenValid = jwtService.validateToken(verificationRequest.jwt(), userDetails);
		Optional<User> byEmail = repository.findByEmail(verificationRequest.username());
		if (isTokenValid)
			return new VerificationResponse(AppConstants.SUCCESS, ((List) userDetails.getAuthorities()).get(0).toString(),
					userDetails.getUsername(), byEmail.get().getId());
		return new VerificationResponse(AppConstants.FAILED, null, null, null);
	}
}

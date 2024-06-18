package com.sankha.userService.services;

import com.sankha.userService.config.AppConstants;
import com.sankha.userService.dto.UserRequest;
import com.sankha.userService.entities.User;
import com.sankha.userService.exceptions.UserAlreadyExistException;
import com.sankha.userService.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
	private final PasswordEncoder passwordEncoder;
	private final UserRepository repository;

	public void register(UserRequest userRequest, UUID referredByUserId) {
		User user = extractUserFromRequest(userRequest);
		if (userExist(user)) {
			throw new UserAlreadyExistException(AppConstants.USER_ALREADY_EXIST);
		}
		User saved = repository.save(user);
//		walletService.createWallet(saved.getId());
//		if (saved != null)
//			sendMessage(saved, CREATE_USER, referralCode);

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
}

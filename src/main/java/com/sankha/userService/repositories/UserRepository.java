package com.sankha.userService.repositories;

import com.sankha.userService.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByEmail(String email);

	Optional<User> findByPhoneNumber(String phoneNumber);
}

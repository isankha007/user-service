package com.sankha.userService.repositories;

import com.sankha.userService.builder.UserBuilder;
import com.sankha.userService.entities.User;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;
	private Validator validator;

	private UserBuilder userBuilder;

	@Autowired
	private UserRepository repository;


	@BeforeEach
	void setup() {
		userBuilder = new UserBuilder();
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void shouldSaveUserToRepository() {
		User user = userBuilder.withEmail("abc@example.com")
				.withNumber("1234567892")
				.withPassword("password")
				.withPreference("Email")
				.build();
		User actual = repository.save(user);
		Assertions.assertEquals(user.getEmail(), actual.getEmail());
		Assertions.assertEquals(user.getPhoneNumber(), actual.getPhoneNumber());
		Assertions.assertEquals(user.getPreference(), actual.getPreference());


	}

	@Test
	void shouldNotSaveUserWithInvalidEmail() {
		User user = userBuilder.withEmail("abcExample.com")
				.withNumber("1234567892")
				.withPassword("password")
				.withPreference("Email")
				.build();

		entityManager.persist(user);
		Set<ConstraintViolation<User>> validate = validator.validate(user);
		Assertions.assertEquals(1, validate.size());

		Assertions.assertThrows(ValidationException.class, () -> entityManager.flush());
	}

	@Test
	void shouldNotSaveUserWithBlankPassword() {
		User user = userBuilder.withEmail("abc@example.com")
				.withNumber("9158986369")
				.withPassword(null)
				.withPreference("Email")
				.build();
		entityManager.persist(user);

		Set<ConstraintViolation<User>> validate = validator.validate(user);
		Assertions.assertEquals(1, validate.size());

		Assertions.assertThrows(ValidationException.class, () -> entityManager.flush());

	}
	@Test
	void shouldNotSaveUserWithInvalidNumber() {
		User user = userBuilder.withEmail("abc@example.com")
				.withNumber("123")
				.withPassword("Password")
				.withPreference("Email")
				.build();
		entityManager.persist(user);

		Set<ConstraintViolation<User>> validate = validator.validate(user);
		Assertions.assertEquals(1, validate.size());

		Assertions.assertThrows(ValidationException.class, () -> entityManager.flush());

	}
}
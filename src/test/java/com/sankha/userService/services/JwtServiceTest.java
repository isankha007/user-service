package com.sankha.userService.services;

import com.sankha.userService.builder.UserBuilder;
import com.sankha.userService.entities.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static java.time.Instant.ofEpochMilli;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class JwtServiceTest {
	@InjectMocks
	private JwtService jwtService;
	@Mock
	private UserDetails userDetails;

	@Mock
	Claims claims;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(jwtService, "SECRETE", "lRgYmPbbwO2YBOKE2caxiVwwG3nDaOvm");

	}

	@Test
	void shouldGenerateToken() {
		User user = new UserBuilder().withId(UUID.randomUUID()).withEmail("abc@example.com")
				.withNumber("9123456780").build();
		String generateToken = jwtService.generateToken(user);

		Assertions.assertNotNull(generateToken);
	}

	@Test
	void shouldExtractCalimsFromToken() {
		String expectedClaim = "abc@example.com";
		String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
				".eyJzdWIiOiJhYmNAZXhhbXBsZS5jb20iLCJuYW1lIjoiSm9obiBEb2UiLCJpYXQiOjE1MTYyMzkwMjJ9.gL_-Olf6RhJNiiGnO4aORqmUt8SUJrzlCp6txshQXw8";

		String calims = jwtService.extractUsername(jwt);


		Assertions.assertNotNull(calims);
		Assertions.assertEquals(expectedClaim, calims);

	}

	@Test
	@Disabled
	void shouldValidateToken() {
		Clock constantClock = Clock.fixed(ofEpochMilli(0), ZoneId.systemDefault());
		Clock clock = Clock.offset(constantClock, Duration.ofSeconds(10));
		String expectedClaim = "abc@example.com";
		String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
				".eyJzdWIiOiJhYmNAZXhhbXBsZS5jb20iLCJuYW1lIjoiSm9obiBEb2UiLCJpYXQiOjE1MTYyMzkwMjJ9.gL_-Olf6RhJNiiGnO4aORqmUt8SUJrzlCp6txshQXw8";
		when(userDetails.getUsername()).thenReturn(expectedClaim);
		when(claims.getExpiration()).thenReturn(new Date());
		// when(Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRETE.getBytes())).build().parseClaimsJws(jwt).getBody()).thenReturn(claims);

		Boolean isValid = jwtService.validateToken(jwt, userDetails);

		Assertions.assertTrue(isValid);
	}
}
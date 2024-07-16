package com.sankha.userService.subscribers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class UserEvent {
	@JsonProperty
	private String email;
	@JsonProperty
	private String phoneNumber;
	@JsonProperty
	private String preference;
	private String eventType;
	private UUID referralCode;
	private UUID id;
}

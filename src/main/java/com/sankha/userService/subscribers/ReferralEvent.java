package com.sankha.userService.subscribers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class ReferralEvent {
    @JsonProperty
    private UUID userId;
    private String eventType;
}

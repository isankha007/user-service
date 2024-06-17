package com.sankha.userService.dto;

import com.sankha.userService.entities.Role;

public record UserRequest(String email,
                          String phoneNumber,
                          String preference,
                          String password,
                          Role role) {

}

package com.sankha.userService.builder;

import com.sankha.userService.entities.Role;
import com.sankha.userService.entities.User;

import java.util.UUID;

public class UserBuilder {
    private UUID id;

    private String email;
    private String phoneNumber;
    private String preference;
    private String password;
    private Role role;

    public UserBuilder withRole(Role role){
        this.role=role;
        return this;
    }
    public UserBuilder withId(UUID userId){
        this.id=userId;
        return this;
    }
    public UserBuilder withNumber(String number){
        this.phoneNumber =number;
        return this;
    }
    public UserBuilder withEmail(String email){
        this.email=email;
        return this;
    }
    public UserBuilder withPreference(String preference){
        this.preference=preference;
        return this;
    }
    public UserBuilder withPassword(String password){
        this.password=password;
        return this;
    }

    public User build(){
        return  new User(
                id,
                email,
                phoneNumber,
                preference,
                password,
                role
        );
    }
}

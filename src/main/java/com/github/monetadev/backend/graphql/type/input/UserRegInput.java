package com.github.monetadev.backend.graphql.type.input;

import lombok.Data;

@Data
public class UserRegInput {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}

package com.github.monetadev.backend.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private Set<String> roles;
}

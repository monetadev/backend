package com.github.monetadev.backend.util;

import com.github.monetadev.backend.dto.UserDTO;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;

import java.util.stream.Collectors;

public class UserMapper {

    /**
     * Helper function to convert a {@link User} to a {@link UserDTO} object.
     * If the {@link User} has not been assigned any {@link Role}, {@link UserDTO} role's set will be null.
     * @param user {@link User} to convert to {@link UserDTO}
     * @return {@link UserDTO} representation of the {@link User} object.
     */
    public static UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        if (user.getRoles() != null) {
            userDTO.setRoles(user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }
        return userDTO;
    }
}

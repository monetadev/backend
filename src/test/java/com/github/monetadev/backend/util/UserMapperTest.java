package com.github.monetadev.backend.util;

import com.github.monetadev.backend.dto.UserDTO;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserMapperTest {
    @Test
    public void testToUserDTO() {
        // Set up a User entity with roles.
        User user = new User();
        user.setUsername("mapperUser");
        user.setEmail("mapper@example.com");
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName("ROLE_USER");
        roles.add(role);
        user.setRoles(roles);

        // Convert the user entity to a DTO.
        UserDTO dto = UserMapper.toUserDTO(user);
        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isEqualTo("mapperUser");
        assertThat(dto.getEmail()).isEqualTo("mapper@example.com");
        assertThat(dto.getRoles()).extracting(Object::toString).isEqualTo("[ROLE_USER]");
    }
}

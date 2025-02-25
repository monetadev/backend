package com.github.monetadev.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.monetadev.backend.model.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    public void testCreateAndFindUser() {
        User user = new User();
        user.setUsername("integrationUser");
        user.setEmail("integration@example.com");
        user.setPassword("password");

        User savedUser = userService.createUser(user);
        assertThat(savedUser.getId()).isNotNull();

        Optional<User> foundUser = userService.findUserById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("integrationUser");
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setUsername("toDeleteUser");
        user.setEmail("delete@example.com");
        user.setPassword("password");
        User savedUser = userService.createUser(user);
        UUID userId = savedUser.getId();

        Optional<User> foundUser = userService.findUserById(userId);
        assertThat(foundUser).isPresent();

        userService.deleteUser(userId);
        Optional<User> deletedUser = userService.findUserById(userId);
        assertThat(deletedUser).isNotPresent();
    }
}

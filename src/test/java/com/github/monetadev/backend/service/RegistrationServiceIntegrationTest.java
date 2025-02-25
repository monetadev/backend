package com.github.monetadev.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.github.monetadev.backend.dto.RegistrationDTO;
import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class RegistrationServiceIntegrationTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Clean out users and roles before each test to ensure independent execution.
     */
    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    /**
     * When no users exist, registration should assign ROLE_ADMIN.
     */
    @Test
    @Transactional
    public void testRegisterNewUserAsFirstUser_ExpectROLE_ADMIN() {
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(0);

        RegistrationDTO registration = new RegistrationDTO();
        registration.setUsername("firstUser");
        registration.setEmail("first@example.com");
        registration.setPassword("secret");
        User registeredUser = registrationService.registerNewUser(registration);

        assertThat(registeredUser.getId()).isNotNull();
        assertThat(registeredUser.getRoles())
                .extracting(Role::getName)
                .contains("ROLE_ADMIN");
    }

    /**
     * When at least one user exists, registration should assign ROLE_USER.
     * Here we simulate a non-empty database by first registering a dummy user.
     */
    @Test
    @Transactional
    public void testRegisterNewUserAsSubsequentUser_ExpectROLE_USER() {
        RegistrationDTO firstRegistration = new RegistrationDTO();
        firstRegistration.setUsername("existingUser");
        firstRegistration.setEmail("existing@example.com");
        firstRegistration.setPassword("secret");
        registrationService.registerNewUser(firstRegistration);
        assertThat(userRepository.count()).isGreaterThan(0);

        RegistrationDTO registration = new RegistrationDTO();
        registration.setUsername("newUser");
        registration.setEmail("new@example.com");
        registration.setPassword("password");
        User registeredUser = registrationService.registerNewUser(registration);

        assertThat(registeredUser.getId()).isNotNull();
        assertThat(registeredUser.getRoles())
                .extracting(Role::getName)
                .contains("ROLE_USER");
    }

    /**
     * Simulate missing roles condition by clearing out roles,
     * then attempt registration to verify that a RoleNotFoundException is thrown.
     */
    @Test
    @Transactional
    public void testRegisterNewUser_RoleNotFound() {
        roleRepository.deleteAll();

        RegistrationDTO registration = new RegistrationDTO();
        registration.setUsername("missingRoleUser");
        registration.setEmail("missingrole@example.com");
        registration.setPassword("secret");

        Throwable thrown = catchThrowable(() -> registrationService.registerNewUser(registration));

        assertThat(thrown)
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("does not exist");
    }
}

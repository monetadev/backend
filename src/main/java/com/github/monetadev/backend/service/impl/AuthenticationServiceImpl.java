package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.exception.AdminProvisioningDisabledException;
import com.github.monetadev.backend.exception.InvalidAdminSecretException;
import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.exception.UserNotAuthenticatedException;
import com.github.monetadev.backend.graphql.type.UserRegInput;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.repository.UserRepository;
import com.github.monetadev.backend.security.jwt.JwtUserDetails;
import com.github.monetadev.backend.service.AuthenticationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.provision.enable:false}")
    private boolean adminProvisionIsEnabled;

    @Value("${admin.provision.secret:}")
    private String adminProvisionSecret;

    public AuthenticationServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() > 0) {
            adminProvisionIsEnabled = false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * The function will first check if required arguments are not null, then
     * ensures that the password matches the following criteria: length is
     * between 8 and 64 characters, contains an uppercase and lowercase letter,
     * contains a number, and contains any of the following special characters:
     * </br>
     * <p>
     * !@#$%^&*()_+-=[]{};':"\|,.<>/?
     * </p>
     * </br>
     * Following that, the {@link UserRepository} is queried to see
     * if the username already exists, then if the email already exists.
     * Once all criteria has been met, the user is saved using the repository.
     */
    @Override
    public User registerNewUser(UserRegInput userRegInput) {
        if (userRegInput.getEmail() == null) {
            throw new IllegalArgumentException("Email is required");
        }
        if (userRegInput.getUsername() == null) {
            throw new IllegalArgumentException("Username is required");
        }
        if (userRegInput.getPassword() == null) {
            throw new IllegalArgumentException("Password is required");
        }
        if (userRegInput.getFirstName() == null || userRegInput.getLastName() == null) {
            throw new IllegalArgumentException("First name and last name is required");
        }
        if (userRegInput.getPassword().length() < 8
                || userRegInput.getPassword().length() > 64
                || !userRegInput.getPassword().matches(".*[a-z].*")
                || !userRegInput.getPassword().matches(".*[A-Z].*")
                || !userRegInput.getPassword().matches(".*[0-9].*")
                || !userRegInput.getPassword().matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("Password is invalid.");
        }
        if (userRepository.findByUsername(userRegInput.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already in use.");
        }
        if (userRepository.existsByEmail(userRegInput.getEmail())) {
            throw new IllegalArgumentException("Invalid email, username, or password.");
        }

        User user = prepareUser(userRegInput);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER does not exist."));
        user.setRoles(Set.of(userRole));

        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User provisionAdmin(UserRegInput userRegInput, String secret) {
        if (!adminProvisionIsEnabled) {
            throw new AdminProvisioningDisabledException("Admin provisioning is disabled");
        }
        if (!adminProvisionSecret.equals(secret)) {
            throw new InvalidAdminSecretException("Invalid admin secret");
        }

        User user = prepareUser(userRegInput);
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_ADMIN does not exist."));
        user.setRoles(Set.of(adminRole));

        adminProvisionIsEnabled = false;
        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new UserNotAuthenticatedException("Principal not authenticated. Please login.");
        }
        if (!(authentication.getPrincipal() instanceof JwtUserDetails userDetails)) {
            throw new UserNotAuthenticatedException("Unexpected principal type");
        }
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotAuthenticatedException("Principal is authenticated, but user could not be found."));
    }

    private User prepareUser(UserRegInput userRegInput) {
        User user = new User();
        user.setUsername(userRegInput.getUsername().toLowerCase());
        user.setEmail(userRegInput.getEmail());
        user.setFirstName(userRegInput.getFirstName());
        user.setLastName(userRegInput.getLastName());
        user.setPassword(passwordEncoder.encode(userRegInput.getPassword()));
        return user;
    }
}

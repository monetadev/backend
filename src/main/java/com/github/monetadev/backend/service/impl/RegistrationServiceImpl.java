package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.graphql.type.UserRegInput;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.repository.UserRepository;
import com.github.monetadev.backend.service.RegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Long initialStartupUserCount;

    public RegistrationServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.initialStartupUserCount = userRepository.count();
    }

    /**
     * Creates a new {@link User} per functional requirements in the backend and
     * best practices for password creation. Errors thrown are intentionally
     * generic as to reduce attack surface.
     * </br></br>
     * The function will first check if required arguments are not null, then
     * ensures that the repeated password matches the first, then ensures that
     * the password matches the following criteria: length is between 8 and 64
     * characters, contains an uppercase and lowercase letter, contains a
     * number, and contains any of the following special characters:
     * </br></br>
     * !@#$%^&*()_+-=[]{};':"\|,.<>/?
     * </br></br>
     * Following that, we will then query the {@link UserRepository} to see
     * if the username already exists, then if the email already exists.
     * Once all criteria has been met, we save the user using the repository.
     *
     * @param userRegInput The {@link UserRegInput} from GraphQL to persist.
     * @return The persisted {@link User} with generated fields.
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
        if (!userRegInput.getPassword().equals(userRegInput.getConfirmPassword())) {
            throw new IllegalArgumentException("Invalid email, username, or password");
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

        User user = new User();
        user.setUsername(userRegInput.getUsername());
        user.setEmail(userRegInput.getEmail());
        user.setFirstName(userRegInput.getFirstName());
        user.setLastName(userRegInput.getLastName());
        user.setPassword(passwordEncoder.encode(userRegInput.getPassword()));

        if (initialStartupUserCount == 0) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RoleNotFoundException("ROLE_ADMIN does not exist."));
            user.setRoles(Set.of(adminRole));
            return userRepository.save(user);
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER does not exist."));
        user.setRoles(Set.of(userRole));

        return userRepository.save(user);
    }

    /**
     * Returns a {@link User} when given valid credentials.
     *
     * @param username The username to authenticate.
     * @param password The password to authenticate.
     * @return The user if valid credentials are supplied.
     * @throws IllegalArgumentException The supplied username doesn't exist, or the password is invalid.
     */
    @Override
    public User authenticate(String username, String password) throws IllegalArgumentException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user.get();
    }
}

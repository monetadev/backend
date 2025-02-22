package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.UserRepository;
import com.github.monetadev.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a {@link User} by their unique identifier.
     *
     * @param id The {@link UUID} of the user to find.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    @Override
    public Optional<User> findUserById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Retrieves a {@link User} by their username.
     *
     * @param username The username to search for.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves a {@link User} by their email address.
     *
     * @param email The email address to search for.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Creates a new {@link User}.
     *
     * @param user The {@link User} entity to persist.
     * @return The persisted {@link User} with generated fields.
     */
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates an existing {@link User}.
     *
     * @param user The {@link User} entity to update.
     * @return The updated {@link User} with generated fields.
     */
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Deletes a {@link User} by their unique identifier.
     *
     * @param id The {@link UUID} of the user to delete.
     */
    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}

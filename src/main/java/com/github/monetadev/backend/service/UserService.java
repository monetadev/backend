package com.github.monetadev.backend.service;

import com.github.monetadev.backend.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    /**
     * Retrieves a {@link User} by their unique identifier.
     * @param id The {@link UUID} of the user to find.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    Optional<User> findUserById(UUID id);

    /**
     * Retrieves a {@link User} by their username.
     * @param username The username to search for.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    Optional<User> findUserByUsername(String username);

    /**
     * Retrieves a {@link User} by their email address.
     * @param email The email address to search for.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Creates a new {@link User}.
     * @param user The {@link User} entity to persist.
     * @return The persisted {@link User} with generated fields.
     */
    User createUser(User user);

    /**
     * Updates an existing {@link User}.
     * @param user The {@link User} entity to update.
     * @return The updated {@link User} with generated fields.
     */
    User updateUser(User user);

    /**
     * Deletes a {@link User} by their unique identifier.
     * @param id The {@link UUID} of the user to delete.
     */
    void deleteUser(UUID id);
}

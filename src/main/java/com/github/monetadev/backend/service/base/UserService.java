package com.github.monetadev.backend.service.base;

import com.github.monetadev.backend.exception.UserNotFoundException;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedResponse;
import com.github.monetadev.backend.model.User;

import java.util.UUID;

public interface UserService {
    PaginatedResponse<User> getAllUsers(int page, int size);

    /**
     * Retrieves a {@link User} by their unique identifier.
     * @param id The {@link UUID} of the user to find.
     * @return The found {@link User}.
     * @throws UserNotFoundException if no user with the given ID exists.
     */
    User findUserById(UUID id) throws UserNotFoundException;

    /**
     * Retrieves a {@link User} by their username.
     * @param username The username to search for.
     * @return The found {@link User}.
     * @throws UserNotFoundException if no user with the given username exists.
     */
    User findUserByUsername(String username) throws UserNotFoundException;

    /**
     * Retrieves a {@link User} by their email address.
     * @param email The email address to search for.
     * @return The found {@link User}.
     * @throws UserNotFoundException if no user with the given email exists.
     */
    User findUserByEmail(String email) throws UserNotFoundException;

    /**
     * Creates a new {@link User}.
     * @param user The {@link User} entity to persist.
     * @return The persisted {@link User} with generated fields.
     */
    User createUser(User user);

    /**
     * Updates an existing {@link User}.
     * @param user The {@link User} entity to update.
     * @return The updated {@link User}.
     * @throws UserNotFoundException if the user to update does not exist.
     */
    User updateUser(User user) throws UserNotFoundException;

    /**
     * Deletes a {@link User} by their unique identifier.
     * @param id The {@link UUID} of the user to delete.
     * @return The uuid of the deleted user.
     * @throws UserNotFoundException if no user with the given ID exists.
     */
    UUID deleteUser(UUID id) throws UserNotFoundException;

    Integer countUsersWithRole(String role);
}

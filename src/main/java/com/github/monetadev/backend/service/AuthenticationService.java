package com.github.monetadev.backend.service;

import com.github.monetadev.backend.graphql.type.UserRegInput;
import com.github.monetadev.backend.model.User;

public interface AuthenticationService {
    /**
     * Creates a new {@link User} per functional requirements in the backend and
     * best practices for password creation. Errors thrown are intentionally
     * generic as to reduce attack surface.
     *
     * @param userRegInput The {@link UserRegInput} from GraphQL to persist.
     * @return The persisted {@link User} with generated fields.
     */
    User registerNewUser(UserRegInput userRegInput);

    User provisionAdmin(UserRegInput userRegInput, String secret);

    /**
     * Returns a {@link User} when given valid credentials.
     *
     * @param username The username to authenticate.
     * @param password The password to authenticate.
     * @return The user if valid credentials are supplied.
     * @throws IllegalArgumentException The supplied username doesn't exist, or the password is invalid.
     */
    User authenticateUser(String username, String password);

    /**
     * Returns the currently authenticated {@link User} in the backend.
     *
     * @return The currently authenticated user, throws an {@link com.github.monetadev.backend.exception.UserNotAuthenticatedException}
     * if no {@link java.security.Principal} is found.
     */
    User getAuthenticatedUser();
}

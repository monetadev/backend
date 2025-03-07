package com.github.monetadev.backend.service;

import com.github.monetadev.backend.graphql.type.UserRegInput;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.UserRepository;

public interface RegistrationService {
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
    User registerNewUser(UserRegInput userRegInput);

    /**
     * Returns a {@link User} when given valid credentials.
     *
     * @param username The username to authenticate.
     * @param password The password to authenticate.
     * @return The user if valid credentials are supplied.
     * @throws IllegalArgumentException The supplied username doesn't exist, or the password is invalid.
     */
    User authenticate(String username, String password) throws IllegalArgumentException;
}

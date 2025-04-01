package com.github.monetadev.backend.service.security;

import com.github.monetadev.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.Authentication;

/**
 * Interface for JWT (JSON Web Token) service operations.
 * Handles generation, validation, and processing of JWT tokens
 * for authentication and authorization purposes.
 */
public interface JwtService {

    /**
     * Generates a JWT token for a given user with claims including username,
     * user ID, email, and authorities derived from roles and privileges.
     *
     * @param user The user for whom to generate the token
     * @return A signed JWT token string
     */
    String generateToken(User user);

    /**
     * Extracts authentication information from a valid JWT token.
     * Creates a Spring Security {@link Authentication} object with user details and authorities.
     *
     * @param token The JWT token to process
     * @return An Authentication object containing user details and granted authorities
     */
    Authentication getAuthentication(String token);

    /**
     * Extracts and returns all claims from a JWT token.
     *
     * @param token The JWT token to parse
     * @return The claims contained in the token
     * @throws JwtException If the token is invalid or cannot be parsed
     */
    Claims getClaims(String token);

    /**
     * Validates whether a JWT token is properly signed and not expired.
     *
     * @param token The JWT token to validate
     * @return Boolean indicating if the token is valid (true) or invalid (false)
     */
    Boolean isValidToken(String token);
}
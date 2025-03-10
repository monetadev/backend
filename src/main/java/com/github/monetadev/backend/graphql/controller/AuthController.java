package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.graphql.type.AuthPayload;
import com.github.monetadev.backend.graphql.type.UserRegInput;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.security.jwt.JwtService;
import com.github.monetadev.backend.service.AuthenticationService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import jakarta.annotation.security.PermitAll;

@DgsComponent
public class AuthController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @DgsMutation
    @PermitAll
    public AuthPayload register(@InputArgument UserRegInput input) {
        User user = authenticationService.registerNewUser(input);
        String token = jwtService.generateToken(user);
        return AuthPayload.builder()
                .user(user)
                .token(token)
                .build();
    }

    @DgsMutation
    @PermitAll
    public AuthPayload registerAdmin(@InputArgument UserRegInput input, @InputArgument String secret) {
        User user = authenticationService.provisionAdmin(input, secret);
        String token = jwtService.generateToken(user);
        return AuthPayload.builder()
                .user(user)
                .token(token)
                .build();
    }

    @DgsMutation
    @PermitAll
    public AuthPayload login(@InputArgument String username, @InputArgument String password) {
        User user = authenticationService.authenticateUser(username, password);
        String token = jwtService.generateToken(user);
        return AuthPayload.builder()
                .user(user)
                .token(token)
                .build();
    }

    @DgsQuery
    public User me() {
        return authenticationService.getAuthenticatedUser();
    }
}

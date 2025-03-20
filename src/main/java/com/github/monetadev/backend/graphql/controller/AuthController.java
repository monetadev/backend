package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.graphql.type.UserRegInput;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.security.jwt.JwtService;
import com.github.monetadev.backend.service.AuthenticationService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@DgsComponent
public class AuthController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final String cookieName;

    public AuthController(AuthenticationService authenticationService,
                          JwtService jwtService,
                          @Autowired String cookieName) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.cookieName = cookieName;
    }

    @DgsMutation
    @PermitAll
    public User register(@InputArgument UserRegInput input,
                         DataFetchingEnvironment env) {
        User user = authenticationService.registerNewUser(input);
        String token = jwtService.generateToken(user);
        env.getGraphQlContext().put(cookieName, token);
        return user;
    }

    @DgsMutation
    @PermitAll
    public User registerAdmin(@InputArgument UserRegInput input,
                              @InputArgument String secret,
                              DataFetchingEnvironment env) {
        User user = authenticationService.provisionAdmin(input, secret);
        String token = jwtService.generateToken(user);
        env.getGraphQlContext().put(cookieName, token);
        return user;
    }

    @DgsMutation
    @PermitAll
    public User login(@InputArgument String username,
                      @InputArgument String password,
                      DataFetchingEnvironment env) {
        User user = authenticationService.authenticateUser(username, password);
        String token = jwtService.generateToken(user);
        env.getGraphQlContext().put(cookieName, token);
        return user;
    }

    @DgsQuery
    public User me() {
        return authenticationService.getAuthenticatedUser();
    }
}

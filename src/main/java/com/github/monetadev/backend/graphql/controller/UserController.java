package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.graphql.type.pagination.PaginatedUser;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.service.UserRoleService;
import com.github.monetadev.backend.service.UserService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

@DgsComponent
public class UserController {
    private final UserService userService;
    private final UserRoleService userRoleService;

    public UserController(UserService userService, UserRoleService userRoleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @DgsQuery
    public User findUserById(@InputArgument UUID id) {
        return userService.findUserById(id);
    }

    @DgsQuery
    public User findUserByUsername(@InputArgument String username) {
        return userService.findUserByUsername(username);
    }

    @DgsQuery
    @PreAuthorize("hasRole('ADMIN')")
    public User findUserByEmail(@InputArgument String email) {
        return userService.findUserByEmail(email);
    }

    @DgsQuery
    @PreAuthorize("hasRole('ADMIN')")
    public PaginatedUser findAllUsers(@InputArgument Integer page, @InputArgument Integer size) {
        return (PaginatedUser) userService.getAllUsers(page, size);
    }

    @DgsMutation
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_MANAGE') or authentication.principal.id == #id")
    public User updateUser(@InputArgument UUID id,
                           @InputArgument String username,
                           @InputArgument String email,
                           @InputArgument String firstName,
                           @InputArgument String lastName) {
        User user = userService.findUserById(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return userService.updateUser(user);
    }

    @DgsMutation
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_MANAGE')")
    public User assignRoleToUser(@InputArgument UUID userId, @InputArgument UUID roleId) {
        return userRoleService.assignRoleToUser(userId, roleId);
    }

    @DgsMutation
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    public User removeRoleFromUser(@InputArgument UUID userId, @InputArgument UUID roleId) {
        return userRoleService.removeRoleFromUser(userId, roleId);
    }
}

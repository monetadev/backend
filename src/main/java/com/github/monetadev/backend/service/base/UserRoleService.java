package com.github.monetadev.backend.service.base;

import com.github.monetadev.backend.model.User;
import com.netflix.graphql.dgs.InputArgument;

import java.util.UUID;

public interface UserRoleService {
    User assignRoleToUser(@InputArgument UUID userId, @InputArgument UUID roleId);
    User removeRoleFromUser(@InputArgument UUID userId, @InputArgument UUID roleId);
}

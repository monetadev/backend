package com.github.monetadev.backend.service.base.impl;

import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.exception.UserNotFoundException;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.repository.UserRepository;
import com.github.monetadev.backend.service.base.UserRoleService;
import com.github.monetadev.backend.service.base.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public UserRoleServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @Override
    public User assignRoleToUser(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleId));

        if (user.getRoles().contains(role)) {
            throw new IllegalArgumentException("Role already assigned to user");
        }

        user.getRoles().add(role);
        role.getUsers().add(user);

        roleRepository.save(role);
        return userRepository.save(user);
    }

    @Override
    public User removeRoleFromUser(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with id: " + userId + " to remove role: " + roleId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Cannot find role with id: " + roleId + " to remove from user: " + userId));

        if (!user.getRoles().contains(role)) {
            throw new IllegalArgumentException("User does not have role: " + role);
        }

        if (isRemovingAdminRole(user, role)) {
            throw new IllegalStateException("Cannot remove default admin role.");
        }

        user.getRoles().remove(role);
        role.getUsers().remove(user);

        roleRepository.save(role);
        return userRepository.save(user);

    }

    private boolean isRemovingAdminRole(User user, Role role) {
        if ("ROLE_ADMIN".equals(role.getName())) {
            return true;
        }

        boolean isLastAdminRoleForUser = user.getRoles().stream()
                .filter(userRole -> "ROLE_ADMIN".equals(userRole.getName()))
                .count() <= 1;

        boolean isRemovingLastAdminRole = userService.countUsersWithRole("ROLE_ADMIN") <= 1;

        return isLastAdminRoleForUser && isRemovingLastAdminRole;
    }
}

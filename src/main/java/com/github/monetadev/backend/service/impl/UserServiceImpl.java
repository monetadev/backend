package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.exception.UserNotFoundException;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedUser;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.repository.UserRepository;
import com.github.monetadev.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public PaginatedUser getAllUsers(int page, int size) {
        Page<User> user = userRepository.findAll(PageRequest.of(page, size));
        return PaginatedUser.of()
                .items(user.getContent())
                .totalPages(user.getTotalPages())
                .totalElements(user.getTotalElements())
                .currentPage(user.getNumber())
                .build();
    }

    /**
     * Retrieves a {@link User} by their unique identifier.
     *
     * @param id The {@link UUID} of the user to find.
     * @return The found {@link User}.
     * @throws UserNotFoundException if no user with the given ID exists.
     */
    @Override
    public User findUserById(UUID id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    /**
     * Retrieves a {@link User} by their username.
     *
     * @param username The username to search for.
     * @return The found {@link User}.
     * @throws UserNotFoundException if no user with the given username exists.
     */
    @Override
    public User findUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    /**
     * Retrieves a {@link User} by their email address.
     *
     * @param email The email address to search for.
     * @return The found {@link User}.
     * @throws UserNotFoundException if no user with the given email exists.
     */
    @Override
    public User findUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * Creates a new {@link User}.
     *
     * @param user The {@link User} entity to persist.
     * @return The persisted {@link User} with generated fields.
     */
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates an existing {@link User}.
     *
     * @param user The {@link User} entity to update.
     * @return The updated {@link User}.
     * @throws UserNotFoundException if the user to update does not exist.
     */
    @Override
    public User updateUser(User user) throws UserNotFoundException {
        if (user.getId() != null && !userRepository.existsById(user.getId())) {
            throw new UserNotFoundException("Cannot update non-existent user with ID: " + user.getId());
        }
        return userRepository.save(user);
    }

    /**
     * Deletes a {@link User} by their unique identifier.
     *
     * @param id The {@link UUID} of the user to delete.
     * @return The uuid of the deleted user.
     * @throws UserNotFoundException if no user with the given ID exists.
     */
    @Override
    public UUID deleteUser(UUID id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Cannot delete non-existent user with ID: " + id);
        }
        userRepository.deleteById(id);
        return id;
    }

    @Override
    public Integer countUsersWithRole(String role) {
        Role userRole = roleRepository.findByName(role)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + role));

        return userRole.getUsers().size();
    }
}

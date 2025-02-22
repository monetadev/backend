package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieves a {@link Role} by its unique identifier.
     *
     * @param id The {@link UUID} of the {@link Role} to find.
     * @return An {@link Optional} containing the found {@link Role}, or empty if not found.
     */
    @Override
    public Optional<Role> findRoleById(UUID id) {
        return roleRepository.findById(id);
    }

    /**
     * Retrieves a {@link Role} by its name.
     *
     * @param name The name of the {@link Role} to find.
     * @return An {@link Optional} containing the found {@link Role}, or empty if not found.
     */
    @Override
    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * Creates a new {@link Role}.
     *
     * @param role The {@link Role} entity to persist.
     * @return The persisted {@link Role} with generated fields.
     */
    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    /**
     * Updates an existing {@link Role}.
     *
     * @param role The {@link Role} entity to update.
     * @return The updated {@link Role} with generated fields.
     */
    @Override
    public Role updateRole(Role role) {
        return roleRepository.save(role);
    }

    /**
     * Deletes a {@link Role} from the database.
     *
     * @param role The {@link Role} entity to delete.
     */
    @Override
    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }
}

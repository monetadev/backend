package com.github.monetadev.backend.service;

import com.github.monetadev.backend.model.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleService {
    /**
     * Retrieves a {@link Role} by its unique identifier.
     * @param id The {@link UUID} of the {@link Role} to find.
     * @return An {@link Optional} containing the found {@link Role}, or empty if not found.
     */
    Optional<Role> findRoleById(UUID id);

    /**
     * Retrieves a {@link Role} by its name.
     * @param name The name of the {@link Role} to find.
     * @return An {@link Optional} containing the found {@link Role}, or empty if not found.
     */
    Optional<Role> findRoleByName(String name);

    /**
     * Creates a new {@link Role}.
     * @param role The {@link Role} entity to persist.
     * @return The persisted {@link Role} with generated fields.
     */
    Role createRole(Role role);

    /**
     * Updates an existing {@link Role}.
     * @param role The {@link Role} entity to update.
     * @return The updated {@link Role} with generated fields.
     */
    Role updateRole(Role role);

    /**
     * Deletes a {@link Role} from the database.
     * @param role The {@link Role} entity to delete.
     */
    void deleteRole(Role role);
}

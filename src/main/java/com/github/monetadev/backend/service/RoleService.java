package com.github.monetadev.backend.service;

import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.model.Role;

import java.util.UUID;

public interface RoleService {
    /**
     * Retrieves a {@link Role} by its unique identifier.
     * @param id The {@link UUID} of the {@link Role} to find.
     * @return The found {@link Role}.
     * @throws RoleNotFoundException if no role with the given ID exists.
     */
    Role findRoleById(UUID id) throws RoleNotFoundException;

    /**
     * Retrieves a {@link Role} by its name.
     * @param name The name of the {@link Role} to find.
     * @return The found {@link Role}.
     * @throws RoleNotFoundException if no role with the given name exists.
     */
    Role findRoleByName(String name) throws RoleNotFoundException;

    /**
     * Creates a new {@link Role}.
     * @param role The {@link Role} entity to persist.
     * @return The persisted {@link Role} with generated fields.
     */
    Role createRole(Role role);

    /**
     * Updates an existing {@link Role}.
     * @param role The {@link Role} entity to update.
     * @return The updated {@link Role}.
     * @throws RoleNotFoundException if the role to update does not exist.
     */
    Role updateRole(Role role) throws RoleNotFoundException;

    /**
     * Deletes a {@link Role} from the database.
     * @param role The {@link Role} entity to delete.
     * @throws RoleNotFoundException if the role to delete does not exist.
     */
    void deleteRole(Role role) throws RoleNotFoundException;
}


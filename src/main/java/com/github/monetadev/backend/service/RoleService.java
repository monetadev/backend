package com.github.monetadev.backend.service;

import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedRole;
import com.github.monetadev.backend.model.Role;

import java.util.UUID;

public interface RoleService {
    /**
     * Retrieves a {@link PaginatedRole} of all {@link Role} objects in the database.
     * @param page The current page in context, base-0.
     * @param size The amount of elements to retrieve.
     * @return A {@link PaginatedRole} containing the {@link java.util.List} of entries. Empty if none exist.
     */
    PaginatedRole getAllRoles(int page, int size);

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
     * Creates a new {@link Role}.
     * @param name The name of the role entity to create and persist.
     * @return The persisted role with generated fields.
     */
    Role createRole(String name);

    /**
     * Updates an existing {@link Role}.
     * @param role The {@link Role} entity to update.
     * @return The updated {@link Role}.
     * @throws RoleNotFoundException if the role to update does not exist.
     */
    Role updateRole(Role role) throws RoleNotFoundException;

    /**
     * Updates an existing {@link Role}.
     * @param id The {@link UUID} of the entity to update.
     * @param name The name of the updated role.
     * @return The updated {@link Role}.
     * @throws RoleNotFoundException If the role to update does not exist.
     */
    Role updateRole(UUID id, String name) throws RoleNotFoundException;

    /**
     * Deletes a {@link Role} from the database.
     * @param id The {@link UUID} of the entity to delete.
     * @return The {@link UUID} of the deleted entity.
     * @throws RoleNotFoundException if the role to delete does not exist.
     */
    UUID deleteRole(UUID id) throws RoleNotFoundException;

    Integer countRolesWithPrivilege(String privilege);
}


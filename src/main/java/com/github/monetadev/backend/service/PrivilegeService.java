package com.github.monetadev.backend.service;

import com.github.monetadev.backend.exception.PrivilegeNotFoundException;
import com.github.monetadev.backend.model.Privilege;

import java.util.UUID;

public interface PrivilegeService {
    /**
     * Retrieves a {@link Privilege} by its unique identifier.
     * @param id The {@link UUID} of the {@link Privilege} to find.
     * @return The found {@link Privilege}.
     * @throws PrivilegeNotFoundException if no privilege with the given ID exists.
     */
    Privilege findPrivilegeById(UUID id) throws PrivilegeNotFoundException;

    /**
     * Retrieves a {@link Privilege} by its name.
     * @param name The name of the {@link Privilege} to find.
     * @return The found {@link Privilege}.
     * @throws PrivilegeNotFoundException if no privilege with the given name exists.
     */
    Privilege findPrivilegeByName(String name) throws PrivilegeNotFoundException;

    /**
     * Creates a new {@link Privilege}.
     * @param privilege The {@link Privilege} entity to persist.
     * @return The persisted {@link Privilege} with generated fields.
     */
    Privilege createPrivilege(Privilege privilege);

    /**
     * Updates an existing {@link Privilege}.
     * @param privilege The {@link Privilege} entity to update.
     * @return The updated {@link Privilege}.
     * @throws PrivilegeNotFoundException if the privilege to update does not exist.
     */
    Privilege updatePrivilege(Privilege privilege) throws PrivilegeNotFoundException;

    /**
     * Deletes a {@link Privilege} by its unique identifiers.
     * @param id The {@link UUID} of the {@link Privilege} to delete.
     * @throws PrivilegeNotFoundException if no privilege with the given ID exists.
     */
    void deletePrivilege(UUID id) throws PrivilegeNotFoundException;
}

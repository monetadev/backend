package com.github.monetadev.backend.service;

import com.github.monetadev.backend.model.Privilege;

import java.util.Optional;
import java.util.UUID;

public interface PrivilegeService {
    /**
     * Retrieves a {@link Privilege} by its unique identifier.
     * @param id The {@link UUID} of the {@link Privilege} to find.
     * @return An {@link Optional} containing the found {@link Privilege}, or empty if not found.
     */
    Optional<Privilege> findPrivilegeById(UUID id);

    /**
     * Retrieves a {@link Privilege} by its name.
     * @param name The name of the {@link Privilege} to find.
     * @return An {@link Optional} containing the found {@link Privilege}, or empty if not found.
     */
    Optional<Privilege> findPrivilegeByName(String name);

    /**
     * Creates a new {@link Privilege}.
     * @param privilege The {@link Privilege} entity to persist.
     * @return The persisted {@link Privilege} with generated fields.
     */
    Privilege createPrivilege(Privilege privilege);

    /**
     * Updates an existing {@link Privilege}.
     * @param privilege The {@link Privilege} entity to update.
     * @return The updated {@link Privilege} with generated fields.
     */
    Privilege updatePrivilege(Privilege privilege);

    /**
     * Deletes a {@link Privilege} by its unique identifiers.
     * @param id The {@link UUID} of the {@link Privilege} to delete.
     */
    void deletePrivilege(UUID id);
}

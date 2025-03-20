package com.github.monetadev.backend.service;

import com.github.monetadev.backend.exception.PrivilegeNotFoundException;
import com.github.monetadev.backend.model.Privilege;

import java.util.List;
import java.util.UUID;

public interface PrivilegeService {
    /**
     * Queries the database to retrieve a {@link List} containing
     * all hard-coded {@link Privilege} entities defined within the project.
     * @return The list of privileges defined within the project.
     */
    List<Privilege> privileges();
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
}

package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.repository.PrivilegeRepository;
import com.github.monetadev.backend.service.PrivilegeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PrivilegeServiceImpl implements PrivilegeService {
    private final PrivilegeRepository privilegeRepository;

    public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    /**
     * Retrieves a {@link Privilege} by its unique identifier.
     *
     * @param id The {@link UUID} of the {@link Privilege} to find.
     * @return An {@link Optional} containing the found {@link Privilege}, or empty if not found.
     */
    @Override
    public Optional<Privilege> findPrivilegeById(UUID id) {
        return privilegeRepository.findById(id);
    }

    /**
     * Retrieves a {@link Privilege} by its name.
     *
     * @param name The name of the {@link Privilege} to find.
     * @return An {@link Optional} containing the found {@link Privilege}, or empty if not found.
     */
    @Override
    public Optional<Privilege> findPrivilegeByName(String name) {
        return privilegeRepository.findByName(name);
    }

    /**
     * Creates a new {@link Privilege}.
     *
     * @param privilege The {@link Privilege} entity to persist.
     * @return The persisted {@link Privilege} with generated fields.
     */
    @Override
    public Privilege createPrivilege(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }

    /**
     * Updates an existing {@link Privilege}.
     *
     * @param privilege The {@link Privilege} entity to update.
     * @return The updated {@link Privilege} with generated fields.
     */
    @Override
    public Privilege updatePrivilege(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }

    /**
     * Deletes a {@link Privilege} by its unique identifiers.
     *
     * @param id The {@link UUID} of the {@link Privilege} to delete.
     */
    @Override
    public void deletePrivilege(UUID id) {
        privilegeRepository.deleteById(id);
    }
}

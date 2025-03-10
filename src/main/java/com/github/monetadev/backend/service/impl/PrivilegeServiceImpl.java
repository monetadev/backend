package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.exception.PrivilegeNotFoundException;
import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.repository.PrivilegeRepository;
import com.github.monetadev.backend.service.PrivilegeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @return The found {@link Privilege}.
     * @throws PrivilegeNotFoundException if no privilege with the given ID exists.
     */
    @Override
    public Privilege findPrivilegeById(UUID id) throws PrivilegeNotFoundException {
        return privilegeRepository.findById(id)
                .orElseThrow(() -> new PrivilegeNotFoundException("Privilege not found with ID: " + id));
    }

    /**
     * Retrieves a {@link Privilege} by its name.
     *
     * @param name The name of the {@link Privilege} to find.
     * @return The found {@link Privilege}.
     * @throws PrivilegeNotFoundException if no privilege with the given name exists.
     */
    @Override
    public Privilege findPrivilegeByName(String name) throws PrivilegeNotFoundException {
        return privilegeRepository.findByName(name)
                .orElseThrow(() -> new PrivilegeNotFoundException("Privilege not found with name: " + name));
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
     * @return The updated {@link Privilege}.
     * @throws PrivilegeNotFoundException if the privilege to update does not exist.
     */
    @Override
    public Privilege updatePrivilege(Privilege privilege) throws PrivilegeNotFoundException {
        if (privilege.getId() != null && !privilegeRepository.existsById(privilege.getId())) {
            throw new PrivilegeNotFoundException("Cannot update non-existent privilege with ID: " + privilege.getId());
        }
        return privilegeRepository.save(privilege);
    }

    /**
     * Deletes a {@link Privilege} by its unique identifiers.
     *
     * @param id The {@link UUID} of the {@link Privilege} to delete.
     * @throws PrivilegeNotFoundException if no privilege with the given ID exists.
     */
    @Override
    public void deletePrivilege(UUID id) throws PrivilegeNotFoundException {
        if (!privilegeRepository.existsById(id)) {
            throw new PrivilegeNotFoundException("Cannot delete non-existent privilege with ID: " + id);
        }
        privilegeRepository.deleteById(id);
    }
}

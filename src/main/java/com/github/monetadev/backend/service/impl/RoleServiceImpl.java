package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @return The found {@link Role}.
     * @throws RoleNotFoundException if no role with the given ID exists.
     */
    @Override
    public Role findRoleById(UUID id) throws RoleNotFoundException {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + id));
    }

    /**
     * Retrieves a {@link Role} by its name.
     *
     * @param name The name of the {@link Role} to find.
     * @return The found {@link Role}.
     * @throws RoleNotFoundException if no role with the given name exists.
     */
    @Override
    public Role findRoleByName(String name) throws RoleNotFoundException {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + name));
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
     * @return The updated {@link Role}.
     * @throws RoleNotFoundException if the role to update does not exist.
     */
    @Override
    public Role updateRole(Role role) throws RoleNotFoundException {
        if (role.getId() != null && !roleRepository.existsById(role.getId())) {
            throw new RoleNotFoundException("Cannot update non-existent role with ID: " + role.getId());
        }
        return roleRepository.save(role);
    }

    /**
     * Deletes a {@link Role} from the database.
     *
     * @param role The {@link Role} entity to delete.
     * @throws RoleNotFoundException if the role to delete does not exist.
     */
    @Override
    public void deleteRole(Role role) throws RoleNotFoundException {
        if (role.getId() != null && !roleRepository.existsById(role.getId())) {
            throw new RoleNotFoundException("Cannot delete non-existent role with ID: " + role.getId());
        }
        roleRepository.delete(role);
    }
}

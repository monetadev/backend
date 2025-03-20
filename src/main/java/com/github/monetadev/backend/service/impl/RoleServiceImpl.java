package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.exception.PrivilegeNotFoundException;
import com.github.monetadev.backend.exception.RoleAlreadyExistsException;
import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedRole;
import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.repository.PrivilegeRepository;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedRole getAllRoles(int page, int size) {
        Page<Role> roles = roleRepository.findAll(PageRequest.of(page, size));

        return PaginatedRole.of()
                .items(roles.getContent())
                .totalPages(roles.getTotalPages())
                .totalElements(roles.getTotalElements())
                .currentPage(roles.getNumber())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role findRoleById(UUID id) throws RoleNotFoundException {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role findRoleByName(String name) throws RoleNotFoundException {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role createRole(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new RoleAlreadyExistsException("Role with name " + role.getName() + " already exists");
        }
        return roleRepository.save(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return createRole(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role updateRole(Role role) throws RoleNotFoundException {
        if (role.getId() != null && !roleRepository.existsById(role.getId())) {
            throw new RoleNotFoundException("Cannot update non-existent role with ID: " + role.getId());
        }
        return roleRepository.save(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role updateRole(UUID id, String name) throws RoleNotFoundException {
        if (id == null || !roleRepository.existsById(id)) {
            throw new RoleNotFoundException("Cannot update non-existent role with ID: " + id);
        }
        Role role = findRoleById(id);
        role.setName(name);
        return roleRepository.save(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID deleteRole(UUID id) throws RoleNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("Cannot delete a null role");
        }
        if (!roleRepository.existsById(id)) {
            throw new RoleNotFoundException("Cannot delete non-existent role with ID: " + id);
        }
        roleRepository.deleteById(id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer countRolesWithPrivilege(String privilege) {
        Privilege userPrivilege = privilegeRepository.findByName(privilege)
                .orElseThrow(() -> new PrivilegeNotFoundException("Privilege not found with name: " + privilege));

        return userPrivilege.getRoles().size();
    }
}

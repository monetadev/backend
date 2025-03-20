package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.exception.PrivilegeNotFoundException;
import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.repository.PrivilegeRepository;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.service.RolePrivilegeService;
import com.github.monetadev.backend.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RolePrivilegeServiceImpl implements RolePrivilegeService {
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RoleService roleService;

    public RolePrivilegeServiceImpl(RoleRepository roleRepository,
                                    PrivilegeRepository privilegeRepository,
                                    RoleService roleService) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.roleService = roleService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role assignPrivilegeToRole(UUID roleId, UUID privilegeId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleId));
        Privilege privilege = privilegeRepository.findById(privilegeId)
                .orElseThrow(() -> new PrivilegeNotFoundException("Privilege not found with id: " + privilegeId));

        if (role.getPrivileges().contains(privilege)) {
            throw new IllegalArgumentException("Privilege already assigned to role");
        }

        role.getPrivileges().add(privilege);
        privilege.getRoles().add(role);

        privilegeRepository.save(privilege);
        return roleRepository.save(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role removePrivilegeFromRole(UUID roleId, UUID privilegeId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Cannot find role with id: " + roleId + " to remove privilege: " + privilegeId));
        Privilege privilege = privilegeRepository.findById(privilegeId)
                .orElseThrow(() -> new PrivilegeNotFoundException("Cannot find privilege with id: " + privilegeId + " to remove from role: " + roleId));

        if (!role.getPrivileges().contains(privilege)) {
            throw new IllegalArgumentException("Role does not have privilege: " + privilege);
        }

        if (isRemovingCriticalPrivilege(role, privilege)) {
            throw new IllegalStateException("Cannot remove critical privilege.");
        }

        role.getPrivileges().remove(privilege);
        privilege.getRoles().remove(role);

        privilegeRepository.save(privilege);
        return roleRepository.save(role);
    }

    private boolean isRemovingCriticalPrivilege(Role role, Privilege privilege) {
        if ("PRIVILEGE_ADMIN".equals(privilege.getName())) {
            return true;
        }

        boolean isLastCriticalPrivilegeForRole = role.getPrivileges().stream()
                .filter(rolePrivilege -> "PRIVILEGE_ADMIN".equals(rolePrivilege.getName()))
                .count() <= 1;

        boolean isRemovingLastCriticalPrivilege = roleService.countRolesWithPrivilege("PRIVILEGE_ADMIN") <= 1;

        return isLastCriticalPrivilegeForRole && isRemovingLastCriticalPrivilege;
    }
}

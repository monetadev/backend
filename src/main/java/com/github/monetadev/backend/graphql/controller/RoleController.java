package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.graphql.type.pagination.PaginatedRole;
import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.service.base.PrivilegeService;
import com.github.monetadev.backend.service.base.RolePrivilegeService;
import com.github.monetadev.backend.service.base.RoleService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@DgsComponent
public class RoleController {
    private final RoleService roleService;
    private final PrivilegeService privilegeService;
    private final RolePrivilegeService rolePrivilegeService;

    public RoleController(RoleService roleService, PrivilegeService privilegeService, RolePrivilegeService rolePrivilegeService) {
        this.roleService = roleService;
        this.privilegeService = privilegeService;
        this.rolePrivilegeService = rolePrivilegeService;
    }

    @DgsQuery
    @PreAuthorize("hasAnyAuthority('ROLE_READ', 'ROLE_WRITE')")
    public Role findRoleById(@InputArgument UUID id) {
        return roleService.findRoleById(id);
    }

    @DgsQuery
    @PreAuthorize("hasAnyAuthority('ROLE_READ', 'ROLE_WRITE')")
    public Role findRoleByName(@InputArgument String name) {
        return roleService.findRoleByName(name);
    }

    @DgsQuery
    @PreAuthorize("hasAnyAuthority('ROLE_READ', 'ROLE_WRITE')")
    public PaginatedRole roles(@InputArgument Integer page,
                               @InputArgument Integer size) {
        return roleService.getAllRoles(page, size);
    }

    @DgsQuery
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public List<Privilege> privileges() {
        return privilegeService.privileges();
    }

    @DgsMutation
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public Role createRole(@InputArgument String name) {
        return roleService.createRole(name);
    }

    @DgsMutation
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public Role updateRole(@InputArgument UUID id,
                           @InputArgument String name) {
        return roleService.updateRole(id, name);
    }

    @DgsMutation
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public UUID deleteRole(@InputArgument UUID id) {
        return roleService.deleteRole(id);
    }

    @DgsMutation
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public Role assignPrivilegeToRole(@InputArgument UUID roleId,
                                      @InputArgument UUID privilegeId) {
        return rolePrivilegeService.assignPrivilegeToRole(roleId, privilegeId);
    }

    @DgsMutation
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public Role removePrivilegeFromRole(@InputArgument UUID roleId,
                                        @InputArgument UUID privilegeId) {
        return rolePrivilegeService.removePrivilegeFromRole(roleId, privilegeId);
    }
}

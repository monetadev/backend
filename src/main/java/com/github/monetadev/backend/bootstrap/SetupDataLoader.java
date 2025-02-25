package com.github.monetadev.backend.bootstrap;

import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.service.PrivilegeService;
import com.github.monetadev.backend.service.RoleService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean isAlreadySetup = false;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PrivilegeService privilegeService;

    @Override
    @Transactional
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        if (isAlreadySetup) {
            return;
        }

        if (!areBasePermissionsPresent()) {
            setupDefaultPrivilegesAndRoles();
        }

        isAlreadySetup = true;
    }

    private boolean areBasePermissionsPresent() {
        return roleService.findRoleByName("ROLE_ADMIN").isPresent() || roleService.findRoleByName("ROLE_USER").isPresent();
    }

    @Transactional
    void setupDefaultPrivilegesAndRoles() {
        Privilege readPrivilege = createPrivilegeIfNotExists("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotExists("WRITE_PRIVILEGE");

        Set<Privilege> adminPrivileges = new HashSet<>(Arrays.asList(readPrivilege, writePrivilege));
        Set<Privilege> userPrivileges = new HashSet<>(Collections.singletonList(readPrivilege));

        createRoleIfNotExists("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotExists("ROLE_USER", userPrivileges);

    }

    @Transactional
    Privilege createPrivilegeIfNotExists(String name) {
        Optional<Privilege> privilegeCandidate = privilegeService.findPrivilegeByName(name);
        if (privilegeCandidate.isEmpty()) {
            Privilege privilege = new Privilege();
            privilege.setName(name);
            return privilegeService.createPrivilege(privilege);
        }
        return privilegeCandidate.get();
    }

    @Transactional
    void createRoleIfNotExists(String name, Set<Privilege> privileges) {
        Optional<Role> role = roleService.findRoleByName(name);
        if (role.isEmpty()) {
            Role newRole = new Role();
            newRole.setName(name);
            newRole.setPrivileges(privileges);
            roleService.createRole(newRole);
        }
    }
}


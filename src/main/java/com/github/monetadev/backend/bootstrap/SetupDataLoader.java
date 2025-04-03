package com.github.monetadev.backend.bootstrap;

import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.repository.PrivilegeRepository;
import com.github.monetadev.backend.repository.RoleRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean isAlreadySetup = false;

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    private static final Map<String, Set<String>> ROLE_PRIVILEGE_MAP = Map.of(
            "ROLE_ADMIN", Set.of(
                    "MANAGE_USERS",
                    "MANAGE_AI_PERMISSIONS",
                    "USER_ADMIN",
                    "ROLE_WRITE",
                    "ROLE_READ",
                    "MANAGE_USER_FLASHCARD"
            ),
            "ROLE_USER", Set.of(
                    "ROLE_READ"
            ),
            "ROLE_MODERATOR", Set.of("READ_PRIVILEGE", "WRITE_PRIVILEGE", "DELETE_PRIVILEGE")
    );

    public SetupDataLoader(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        if (isAlreadySetup) {
            return;
        }

        setupRolesAndPrivileges();
        isAlreadySetup = true;
    }

    @Transactional
    void setupRolesAndPrivileges() {
        Map<String, Privilege> privilegeMap = createOrGetPrivileges(ROLE_PRIVILEGE_MAP.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        ROLE_PRIVILEGE_MAP.forEach((roleName, privilegeNames) ->
                createRoleIfNotExists(roleName, privilegeNames.stream()
                        .map(privilegeMap::get)
                        .collect(Collectors.toSet())));
    }

    @Transactional
    Map<String, Privilege> createOrGetPrivileges(Set<String> privilegeNames) {
        Map<String, Privilege> existingPrivileges = privilegeRepository.findAll().stream()
                .collect(Collectors.toMap(Privilege::getName, p -> p));

        Map<String, Privilege> privilegeMap = new HashMap<>(existingPrivileges);

        for (String privilegeName : privilegeNames) {
            privilegeMap.computeIfAbsent(privilegeName, name -> {
                Privilege newPrivilege = new Privilege();
                newPrivilege.setName(name);
                return privilegeRepository.save(newPrivilege);
            });
        }

        return privilegeMap;
    }

    @Transactional
    void createRoleIfNotExists(String name, Set<Privilege> privileges) {
        roleRepository.findByName(name).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(name);
            newRole.setPrivileges(privileges);
            return roleRepository.save(newRole);
        });
    }
}

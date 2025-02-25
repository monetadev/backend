package com.github.monetadev.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.repository.RoleRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class RoleServiceIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @BeforeEach
    public void setUp() {
        roleRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCreateAndFindRole() {
        Role role = new Role();
        role.setName("ROLE_TEST");
        Role savedRole = roleService.createRole(role);
        assertThat(savedRole.getId()).isNotNull();

        Optional<Role> foundRole = roleService.findRoleById(savedRole.getId());
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo("ROLE_TEST");

        Optional<Role> foundByName = roleService.findRoleByName("ROLE_TEST");
        assertThat(foundByName).isPresent();
    }

    @Test
    @Transactional
    public void testUpdateAndDeleteRole() {
        Role role = new Role();
        role.setName("ROLE_BEFORE_UPDATE");
        Role savedRole = roleService.createRole(role);
        UUID id = savedRole.getId();
        assertThat(id).isNotNull();

        savedRole.setName("ROLE_AFTER_UPDATE");
        Role updatedRole = roleService.updateRole(savedRole);
        assertThat(updatedRole.getName()).isEqualTo("ROLE_AFTER_UPDATE");

        roleService.deleteRole(updatedRole);
        Optional<Role> deletedRole = roleService.findRoleById(id);
        assertThat(deletedRole).isNotPresent();
    }
}

package com.github.monetadev.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.repository.PrivilegeRepository;
import java.util.Optional;

import com.github.monetadev.backend.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class PrivilegeServiceIntegrationTest {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        roleRepository.deleteAll();
        privilegeRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCreateAndFindPrivilege() {
        Privilege privilege = new Privilege();
        privilege.setName("READ_PRIVILEGE_TEST");
        Privilege savedPrivilege = privilegeService.createPrivilege(privilege);
        assertThat(savedPrivilege.getId()).isNotNull();

        Optional<Privilege> foundPrivilege = privilegeService.findPrivilegeById(savedPrivilege.getId());
        assertThat(foundPrivilege).isPresent();
        assertThat(foundPrivilege.get().getName()).isEqualTo("READ_PRIVILEGE_TEST");

        Optional<Privilege> foundByName = privilegeService.findPrivilegeByName("READ_PRIVILEGE_TEST");
        assertThat(foundByName).isPresent();
    }

    @Test
    @Transactional
    public void testUpdateAndDeletePrivilege() {
        Privilege privilege = new Privilege();
        privilege.setName("WRITE_PRIVILEGE_TEST");
        Privilege savedPrivilege = privilegeService.createPrivilege(privilege);

        savedPrivilege.setName("UPDATED_WRITE_PRIVILEGE");
        Privilege updatedPrivilege = privilegeService.updatePrivilege(savedPrivilege);
        assertThat(updatedPrivilege.getName()).isEqualTo("UPDATED_WRITE_PRIVILEGE");

        privilegeService.deletePrivilege(updatedPrivilege.getId());
        Optional<Privilege> deletedPrivilege = privilegeService.findPrivilegeById(updatedPrivilege.getId());
        assertThat(deletedPrivilege).isNotPresent();
    }
}

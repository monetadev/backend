package com.github.monetadev.backend.graphql;

import com.github.monetadev.backend.graphql.type.pagination.PaginatedRole;
import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.service.base.PrivilegeService;
import com.github.monetadev.backend.service.base.RolePrivilegeService;
import com.github.monetadev.backend.service.base.RoleService;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RoleControllerTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private PrivilegeService privilegeService;

    @MockitoBean
    private RolePrivilegeService rolePrivilegeService;

    @Test
    @WithMockUser(authorities = {"ROLE_READ"})
    public void testFindRoleById() {
        UUID roleId = UUID.randomUUID();
        Role role = new Role();
        role.setId(roleId);
        role.setName("ROLE_USER");

        when(roleService.findRoleById(roleId)).thenReturn(role);

        @Language("GraphQL") String query =
                "query { findRoleById(id: \"%s\") { id name } }".formatted(roleId);
        String roleName = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.findRoleById.name");
        assertThat(roleName).isEqualTo("ROLE_USER");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_READ"})
    public void testFindRoleByName() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("ROLE_MODERATOR");

        when(roleService.findRoleByName("ROLE_MODERATOR")).thenReturn(role);

        @Language("GraphQL") String query =
                "query { findRoleByName(name: \"ROLE_MODERATOR\") { id name } }";
        String roleName = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.findRoleByName.name");
        assertThat(roleName).isEqualTo("ROLE_MODERATOR");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_READ"})
    public void testRoles() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("ROLE_USER");

        PaginatedRole paginatedRole = PaginatedRole.of()
                .items(Collections.singletonList(role))
                .currentPage(0)
                .totalElements(1)
                .totalPages(1)
                .build();

        when(roleService.getAllRoles(0, 10)).thenReturn(paginatedRole);

        @Language("GraphQL") String query =
                "query { roles(page: 0, size: 10) { items { name } pageInfo { totalElements currentPage totalPages } } }";
        int totalElements = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.roles.pageInfo.totalElements");
        assertThat(totalElements).isEqualTo(1);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_WRITE"})
    public void testPrivileges() {
        Privilege privilege = new Privilege();
        privilege.setId(UUID.randomUUID());
        privilege.setName("READ_PRIVILEGE");

        when(privilegeService.privileges()).thenReturn(Collections.singletonList(privilege));

        String query = "query { privileges { name } }";
        String privilegeName = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.privileges[0].name");
        assertThat(privilegeName).isEqualTo("READ_PRIVILEGE");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_WRITE"})
    public void testCreateRole() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("ROLE_NEW");

        when(roleService.createRole("ROLE_NEW")).thenReturn(role);

        @Language("GraphQL") String mutation = "mutation { createRole(name: \"ROLE_NEW\") { id name } }";
        String roleName = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.createRole.name");
        assertThat(roleName).isEqualTo("ROLE_NEW");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_WRITE"})
    public void testUpdateRole() {
        UUID roleId = UUID.randomUUID();
        Role role = new Role();
        role.setId(roleId);
        role.setName("ROLE_UPDATED");

        when(roleService.updateRole(roleId, "ROLE_UPDATED")).thenReturn(role);

        @Language("GraphQL") String mutation =
                "mutation { updateRole(id: \"%s\", name: \"ROLE_UPDATED\") { id name } }".formatted(roleId);
        String roleName = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.updateRole.name");
        assertThat(roleName).isEqualTo("ROLE_UPDATED");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_WRITE"})
    public void testDeleteRole() {
        UUID roleId = UUID.randomUUID();
        when(roleService.deleteRole(roleId)).thenReturn(roleId);

        String mutation = String.format("mutation { deleteRole(id: \"%s\") }", roleId);
        String deletedId = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.deleteRole");
        assertThat(UUID.fromString(deletedId)).isEqualTo(roleId);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_WRITE"})
    public void testAssignPrivilegeToRole() {
        UUID roleId = UUID.randomUUID();
        UUID privilegeId = UUID.randomUUID();
        Role role = new Role();
        role.setId(roleId);
        role.setName("ROLE_USER");

        when(rolePrivilegeService.assignPrivilegeToRole(roleId, privilegeId)).thenReturn(role);

        @Language("GraphQL") String mutation =
                "mutation { assignPrivilegeToRole(roleId: \"%s\", privilegeId: \"%s\") { name } }".formatted(roleId, privilegeId);
        String roleName = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.assignPrivilegeToRole.name");
        assertThat(roleName).isEqualTo("ROLE_USER");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_WRITE"})
    public void testRemovePrivilegeFromRole() {
        UUID roleId = UUID.randomUUID();
        UUID privilegeId = UUID.randomUUID();
        Role role = new Role();
        role.setId(roleId);
        role.setName("ROLE_USER");

        when(rolePrivilegeService.removePrivilegeFromRole(roleId, privilegeId)).thenReturn(role);

        @Language("GraphQL") String mutation =
                "mutation { removePrivilegeFromRole(roleId: \"%s\", privilegeId: \"%s\") { name } }".formatted(roleId, privilegeId);
        String roleName = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.removePrivilegeFromRole.name");
        assertThat(roleName).isEqualTo("ROLE_USER");
    }
}

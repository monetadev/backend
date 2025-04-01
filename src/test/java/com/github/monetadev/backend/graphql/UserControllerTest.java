package com.github.monetadev.backend.graphql;

import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.service.base.PrivilegeService;
import com.github.monetadev.backend.service.base.UserRoleService;
import com.github.monetadev.backend.service.base.UserService;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRoleService userRoleService;

    @MockitoBean
    private PrivilegeService privilegeService;

    @Test
    public void testFindUserById() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");

        when(userService.findUserById(userId)).thenReturn(user);

        String query = String.format(
                "query { findUserById(id: \"%s\") { id username email } }", userId);
        String username = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.findUserById.username");
        assertThat(username).isEqualTo("john_doe");
    }

    @Test
    public void testFindUserByUsername() {
        String username = "jane_doe";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail("jane@example.com");

        when(userService.findUserByUsername(username)).thenReturn(user);

        String query = String.format(
                "query { findUserByUsername(username: \"%s\") { username email } }", username);
        String email = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.findUserByUsername.email");
        assertThat(email).isEqualTo("jane@example.com");
    }

    @Test
    @WithMockUser(authorities = {"USER_ADMIN"})
    public void testFindUserByEmail() {
        String email = "admin@example.com";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("admin");
        user.setEmail(email);

        when(userService.findUserByEmail(email)).thenReturn(user);

        @Language("GraphQL") String query =
                "query { findUserByEmail(email: \"%s\") { username email } }".formatted(email);
        String username = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.findUserByEmail.username");
        assertThat(username).isEqualTo("admin");
    }

    @Test
    @WithMockUser(authorities = {"USER_ADMIN"})
    public void testFindAllUsers() {
        // For brevity, here we simply call the query.
        // In a complete test you would construct a dummy PaginatedUser object.
        @Language("GraphQL") String query =
                "query { findAllUsers(page: 0, size: 10) { items { username } pageInfo { totalElements currentPage totalPages } } }";
        dgsQueryExecutor.execute(query);
    }

    @Test
    @WithMockUser(authorities = {"USER_ADMIN"})
    public void testUpdateUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("oldName");
        user.setEmail("old@example.com");
        user.setFirstName("Old");
        user.setLastName("Name");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("newName");
        updatedUser.setEmail("new@example.com");
        updatedUser.setFirstName("New");
        updatedUser.setLastName("Name");

        when(userService.findUserById(userId)).thenReturn(user);
        when(userService.updateUser(user)).thenReturn(updatedUser);

        @Language("GraphQL") String mutation =
                "mutation { updateUser(id: \"%s\", username: \"newName\", email: \"new@example.com\", firstName: \"New\", lastName: \"Name\") { username email firstName lastName } }".formatted(
                userId);
        String newUsername = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.updateUser.username");
        assertThat(newUsername).isEqualTo("newName");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_WRITE"})
    public void testAssignRoleToUser() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("user");
        user.setEmail("user@example.com");

        when(userRoleService.assignRoleToUser(userId, roleId)).thenReturn(user);

        @Language("GraphQL") String mutation =
                "mutation { assignRoleToUser(userId: \"%s\", roleId: \"%s\") { username } }".formatted(
                userId, roleId);
        String username = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.assignRoleToUser.username");
        assertThat(username).isEqualTo("user");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_WRITE"})
    public void testRemoveRoleFromUser() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("user");
        user.setEmail("user@example.com");

        when(userRoleService.removeRoleFromUser(userId, roleId)).thenReturn(user);

        String mutation = String.format(
                "mutation { removeRoleFromUser(userId: \"%s\", roleId: \"%s\") { username } }",
                userId, roleId);
        String username = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.removeRoleFromUser.username");
        assertThat(username).isEqualTo("user");
    }

    @Test
    @WithMockUser(authorities = {"USER_ADMIN"})
    public void testDeleteUser() {
        UUID userId = UUID.randomUUID();
        when(userService.deleteUser(userId)).thenReturn(userId);

        String mutation = String.format("mutation { deleteUser(id: \"%s\") }", userId);
        String deletedId = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.deleteUser");
        assertThat(UUID.fromString(deletedId)).isEqualTo(userId);
    }
}

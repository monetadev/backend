package com.github.monetadev.backend.graphql;

import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.security.jwt.JwtService;
import com.github.monetadev.backend.service.AuthenticationService;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthControllerTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    public void testRegister() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setUsername("newuser");
        user.setEmail("newuser@example.com");

        when(authenticationService.registerNewUser(any())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("dummyToken");

        @Language("GraphQL") String mutation =
                "mutation { register(input: { username: \"newuser\", email: \"newuser@example.com\", firstName: \"New\", lastName: \"User\", password: \"Password1!\" }) { id username email } }";
        String username = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.register.username");
        assertThat(username).isEqualTo("newuser");
    }

    @Test
    public void testRegisterAdmin() {
        User admin = new User();
        UUID adminId = UUID.randomUUID();
        admin.setId(adminId);
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");

        when(authenticationService.provisionAdmin(any(), eq("secret123"))).thenReturn(admin);
        when(jwtService.generateToken(admin)).thenReturn("adminToken");

        @Language("GraphQL") String mutation =
                "mutation { registerAdmin(input: { username: \"admin\", email: \"admin@example.com\", firstName: \"Admin\", lastName: \"User\", password: \"Password1!\" }, secret: \"secret123\") { username email } }";
        String username = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.registerAdmin.username");
        assertThat(username).isEqualTo("admin");
    }

    @Test
    public void testLogin() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setUsername("loginuser");
        user.setEmail("login@example.com");

        when(authenticationService.authenticateUser("loginuser", "Password1!")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("loginToken");

        @Language("GraphQL") String mutation =
                "mutation { login(username: \"loginuser\", password: \"Password1!\") { username email } }";
        String username = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.login.username");
        assertThat(username).isEqualTo("loginuser");
    }

    @Test
    public void testMe() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setUsername("currentuser");
        user.setEmail("current@example.com");

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        @Language("GraphQL") String query = "query { me { username email } }";
        String username = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.me.username");
        assertThat(username).isEqualTo("currentuser");
    }
}

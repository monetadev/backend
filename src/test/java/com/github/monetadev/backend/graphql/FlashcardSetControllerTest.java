package com.github.monetadev.backend.graphql;

import com.github.monetadev.backend.graphql.type.FlashcardSetInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedFlashcardSet;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.security.jwt.JwtUserDetails;
import com.github.monetadev.backend.service.base.FlashcardSetService;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import graphql.ExecutionResult;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FlashcardSetControllerTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockitoBean
    private FlashcardSetService flashcardSetService;

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testFindFlashcardSetById() {
        UUID setId = UUID.randomUUID();
        FlashcardSet flashcardSet = new FlashcardSet();
        flashcardSet.setId(setId);
        flashcardSet.setTitle("Test Set");
        flashcardSet.setDescription("Description");

        when(flashcardSetService.findFlashcardSetById(setId)).thenReturn(flashcardSet);

        @Language("GraphQL") String query =
                "query { findFlashcardSetById(id: \"%s\") { id title description } }"
                        .formatted(setId);
        String title = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.findFlashcardSetById.title");
        assertThat(title).isEqualTo("Test Set");
    }

    @Test
    public void testFindFlashcardSetByAuthorId() {
        UUID authorId = UUID.randomUUID();
        FlashcardSet flashcardSet = new FlashcardSet();
        flashcardSet.setId(UUID.randomUUID());
        flashcardSet.setTitle("Author Set");
        flashcardSet.setDescription("Description");

        PaginatedFlashcardSet paginatedFlashcardSet = PaginatedFlashcardSet.of()
                .items(Collections.singletonList(flashcardSet))
                .currentPage(0)
                .totalElements(1)
                .totalPages(1)
                .build();

        when(flashcardSetService.findFlashcardSetsByAuthorId(authorId, 0, 10)).thenReturn(paginatedFlashcardSet);

        @Language("GraphQL") String query =
                "query { findFlashcardSetByAuthorId(id: \"%s\", page: 0, size: 10) { items { title } pageInfo { totalElements currentPage totalPages } } }"
                        .formatted(authorId);
        int totalElements = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.findFlashcardSetByAuthorId.pageInfo.totalElements");
        assertThat(totalElements).isEqualTo(1);
    }

    @Test
    public void testFindPublicFlashcardSets() {
        FlashcardSet flashcardSet = new FlashcardSet();
        flashcardSet.setId(UUID.randomUUID());
        flashcardSet.setIsPublic(true);
        flashcardSet.setTitle("Public Set");
        flashcardSet.setDescription("Public Description");

        PaginatedFlashcardSet paginatedFlashcardSet = PaginatedFlashcardSet.of()
                .items(Collections.singletonList(flashcardSet))
                .currentPage(0)
                .totalElements(1)
                .totalPages(1)
                .build();

        when(flashcardSetService.findPublicFlashcardSets(0, 10)).thenReturn(paginatedFlashcardSet);

        @Language("GraphQL") String query =
                "query { findPublicFlashcardSets(filter: { searchTerm: \"\" }, page: 0, size: 10) { items { title } pageInfo { totalElements currentPage totalPages } } }";
        int totalElements = dgsQueryExecutor.executeAndExtractJsonPath(query, "$.data.findPublicFlashcardSets.pageInfo.totalElements");
        assertThat(totalElements).isEqualTo(1);
    }

    @Test
    @WithMockUser
    public void testCreateFlashcardSet() {
        FlashcardSetInput input = new FlashcardSetInput();
        input.setTitle("New Set");
        input.setDescription("New Description");
        input.setIsPublic(true);
        input.setFlashcards(Collections.emptyList());

        FlashcardSet createdSet = new FlashcardSet();
        createdSet.setId(UUID.randomUUID());
        createdSet.setTitle("New Set");
        createdSet.setDescription("New Description");

        when(flashcardSetService.createFlashcardSet(input)).thenReturn(createdSet);

        @Language("GraphQL") String mutation =
                "mutation { createFlashcardSet(flashcardSetInput: { title: \"New Set\", description: \"New Description\", isPublic: true, flashcards: [] }) { id title description } }";
        String title = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.createFlashcardSet.title");
        assertThat(title).isEqualTo("New Set");
    }

    @Test
    @WithMockUser
    public void testUpdateFlashcardSet() {
        UUID setId = UUID.randomUUID();
        FlashcardSetInput input = new FlashcardSetInput();
        input.setTitle("Updated Set");
        input.setDescription("Updated Description");
        input.setIsPublic(false);
        input.setFlashcards(Collections.emptyList());

        FlashcardSet updatedSet = new FlashcardSet();
        updatedSet.setId(setId);
        updatedSet.setTitle("Updated Set");
        updatedSet.setDescription("Updated Description");

        when(flashcardSetService.updateFlashcardSet(setId, input)).thenReturn(updatedSet);

        @Language("GraphQL") String mutation =
                "mutation { updateFlashcardSet(id: \"%s\", flashcardSetInput: { title: \"Updated Set\", description: \"Updated Description\", isPublic: false, flashcards: [] }) { id title description } }"
                        .formatted(setId);
        String title = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.updateFlashcardSet.title");
        assertThat(title).isEqualTo("Updated Set");
    }

    /**
     * Test authorized by self-access:
     * The authenticated user's ID matches the userId provided in the mutation.
     */
    @Test
    public void testDeleteFlashcardSet_authorized_bySelfAccess() {
        UUID userId = UUID.randomUUID();
        UUID setId = UUID.randomUUID();

        when(flashcardSetService.deleteFlashcardSet(setId)).thenReturn("Set Title");

        // Set authentication with a principal whose id matches userId.
        JwtUserDetails userDetails = new JwtUserDetails(userId, "testuser", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        @Language("GraphQL") String mutation =
                "mutation { deleteFlashcardSet(userId: \"%s\", setId: \"%s\") }"
                        .formatted(userId, setId);
        String title = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.deleteFlashcardSet");
        assertThat(title).isEqualTo("Set Title");
    }

    /**
     * Test authorized by authority:
     * The authenticated user does not match the provided userId but has the MANAGE_USER_FLASHCARD authority.
     */
    @Test
    public void testDeleteFlashcardSet_authorized_byAuthority() {
        UUID providedUserId = UUID.randomUUID();
        UUID setId = UUID.randomUUID();

        when(flashcardSetService.deleteFlashcardSet(setId)).thenReturn("Set Title");

        // Create an authenticated user with a different id but with the required authority.
        JwtUserDetails userDetails = new JwtUserDetails(UUID.randomUUID(), "adminuser", "password",
                List.of(new SimpleGrantedAuthority("MANAGE_USER_FLASHCARD")));
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        @Language("GraphQL") String mutation =
                "mutation { deleteFlashcardSet(userId: \"%s\", setId: \"%s\") }"
                        .formatted(providedUserId, setId);
        String title = dgsQueryExecutor.executeAndExtractJsonPath(mutation, "$.data.deleteFlashcardSet");
        assertThat(title).isEqualTo("Set Title");
    }

    /**
     * Test unauthorized access:
     * The authenticated user's ID does not match the provided userId and they lack the required authority.
     */
    @Test
    public void testDeleteFlashcardSet_unauthorized() {
        UUID providedUserId = UUID.randomUUID();
        UUID setId = UUID.randomUUID();

        // Set an authenticated user whose id is different and has no special authority.
        JwtUserDetails userDetails = new JwtUserDetails(UUID.randomUUID(), "someuser", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        @Language("GraphQL") String mutation =
                "mutation { deleteFlashcardSet(userId: \"%s\", setId: \"%s\") }"
                        .formatted(providedUserId, setId);
        ExecutionResult result = dgsQueryExecutor.execute(mutation);
        // We expect an authorization error so data should be null and errors present.
        assertThat(result.getErrors()).isNotEmpty();
    }
}

package com.github.monetadev.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.monetadev.backend.model.Flashcard;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.model.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class FlashcardAndFlashcardSetServiceIntegrationTest {

    @Autowired
    private FlashcardSetService flashcardSetService;

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private UserService userService;

    private User testAuthor;

    @BeforeEach
    @Transactional
    public void setUp() {
        User user = new User();
        user.setUsername("flashcardAuthor");
        user.setEmail("author@example.com");
        user.setPassword("password");
        testAuthor = userService.createUser(user);
    }

    @Test
    @Transactional
    public void testCreateAndRetrieveFlashcardSet() {
        FlashcardSet flashcardSet = new FlashcardSet();
        flashcardSet.setTitle("Integration Test Set");
        flashcardSet.setIsPublic(true);
        flashcardSet.setAuthor(testAuthor);

        FlashcardSet savedSet = flashcardSetService.createFlashcardSet(flashcardSet);
        assertThat(savedSet.getId()).isNotNull();

        Optional<FlashcardSet> optionalSet = flashcardSetService.findFlashcardSetById(savedSet.getId());
        assertThat(optionalSet).isPresent();
        assertThat(optionalSet.get().getTitle()).isEqualTo("Integration Test Set");
    }

    @Test
    @Transactional
    public void testCreateAndRetrieveFlashcard() {
        FlashcardSet flashcardSet = new FlashcardSet();
        flashcardSet.setTitle("Flashcard Set For Cards");
        flashcardSet.setIsPublic(true);
        flashcardSet.setAuthor(testAuthor);
        FlashcardSet savedSet = flashcardSetService.createFlashcardSet(flashcardSet);

        Flashcard flashcard = new Flashcard();
        flashcard.setTerm("Sample Term");
        flashcard.setDefinition("Sample Definition");
        flashcard.setPosition(1);
        flashcard.setFlashcardSet(savedSet);

        Flashcard savedFlashcard = flashcardService.createFlashcard(flashcard);
        assertThat(savedFlashcard.getId()).isNotNull();

        List<Flashcard> flashcards = flashcardService.findFlashcardsBySetId(savedSet.getId());
        assertThat(flashcards).isNotEmpty();
        assertThat(flashcards.getFirst().getTerm()).isEqualTo("Sample Term");
    }
}

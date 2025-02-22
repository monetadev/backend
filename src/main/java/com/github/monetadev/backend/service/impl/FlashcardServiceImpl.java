package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.model.Flashcard;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.repository.FlashcardRepository;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.service.FlashcardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardRepository flashcardRepository;

    public FlashcardServiceImpl(FlashcardRepository flashcardRepository) {
        this.flashcardRepository = flashcardRepository;
    }

    /**
     * Retrieves a {@link Flashcard} by its {@link UUID}.
     *
     * @param id The {@link UUID} of the {@link Flashcard} to find.
     * @return An {@link Optional} containing the found {@link Flashcard}, or empty if not found.
     */
    @Override
    public Optional<Flashcard> findFlashcardById(UUID id) {
        return flashcardRepository.findById(id);
    }

    /**
     * Retrieves all {@link Flashcard} objects associated with a specific {@link FlashcardSet}.
     *
     * @param setId The {@link UUID} of the {@link FlashcardSet} to query for.
     * @return A {@link List} of {@link Flashcard} objects belonging to the specified {@link FlashcardSet}, may be empty.
     */
    @Override
    public List<Flashcard> findFlashcardsBySetId(UUID setId) {
        return flashcardRepository.findAllByFlashcardSetId(setId);
    }

    /**
     * Creates a new {@link Flashcard}.
     *
     * @param flashcard The {@link Flashcard} entity to persist.
     * @return The persisted {@link Flashcard} with generated fields.
     */
    @Override
    public Flashcard createFlashcard(Flashcard flashcard) {
        return flashcardRepository.save(flashcard);
    }

    /**
     * Updates an existing {@link Flashcard}.
     *
     * @param flashcard The {@link Flashcard} entity to update.
     * @return The updated {@link Flashcard} with generated fields.
     */
    @Override
    public Flashcard updateFlashcard(Flashcard flashcard) {
        return flashcardRepository.save(flashcard);
    }

    /**
     * Deletes a {@link Flashcard} from the database.
     *
     * @param flashcard The {@link Flashcard} entity to delete.
     */
    @Override
    public void deleteFlashcard(Flashcard flashcard) {
        flashcardRepository.delete(flashcard);
    }
}

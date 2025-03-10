package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.exception.FlashcardNotFoundException;
import com.github.monetadev.backend.model.Flashcard;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.repository.FlashcardRepository;
import com.github.monetadev.backend.service.FlashcardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
     * @return The found {@link Flashcard}.
     * @throws FlashcardNotFoundException if no flashcard with the given ID exists.
     */
    @Override
    public Flashcard findFlashcardById(UUID id) throws FlashcardNotFoundException {
        return flashcardRepository.findById(id)
                .orElseThrow(() -> new FlashcardNotFoundException("Flashcard not found with ID: " + id));
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
     * @return The updated {@link Flashcard}.
     * @throws FlashcardNotFoundException if the flashcard to update does not exist.
     */
    @Override
    public Flashcard updateFlashcard(Flashcard flashcard) throws FlashcardNotFoundException {
        if (flashcard.getId() != null && !flashcardRepository.existsById(flashcard.getId())) {
            throw new FlashcardNotFoundException("Cannot update non-existent flashcard with ID: " + flashcard.getId());
        }
        return flashcardRepository.save(flashcard);
    }

    /**
     * Deletes a {@link Flashcard} from the database.
     *
     * @param flashcard The {@link Flashcard} entity to delete.
     * @throws FlashcardNotFoundException if the flashcard to delete does not exist.
     */
    @Override
    public void deleteFlashcard(Flashcard flashcard) throws FlashcardNotFoundException {
        if (flashcard.getId() != null && !flashcardRepository.existsById(flashcard.getId())) {
            throw new FlashcardNotFoundException("Cannot delete non-existent flashcard with ID: " + flashcard.getId());
        }
        flashcardRepository.delete(flashcard);
    }
}

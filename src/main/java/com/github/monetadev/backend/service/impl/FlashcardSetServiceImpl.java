package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.FlashcardSetRepository;
import com.github.monetadev.backend.service.FlashcardSetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FlashcardSetServiceImpl implements FlashcardSetService {
    private final FlashcardSetRepository flashcardSetRepository;

    public FlashcardSetServiceImpl(FlashcardSetRepository flashcardSetRepository) {
        this.flashcardSetRepository = flashcardSetRepository;
    }

    /**
     * Retrieves a {@link FlashcardSet} by its unique identifier.
     *
     * @param id The {@link UUID} of the {@link FlashcardSet} to find.
     * @return An {@link Optional} containing the found {@link FlashcardSet}, or empty if not found.
     */
    @Override
    public Optional<FlashcardSet> findFlashcardSetById(UUID id) {
        return flashcardSetRepository.findById(id);
    }

    /**
     * Retrieves all {@link FlashcardSet} objects created by a specific {@link User}.
     *
     * @param authorId The {@link UUID} of the author {@link User}.
     * @return A {@link List} of {@link FlashcardSet}, empty if none found.
     */
    @Override
    public List<FlashcardSet> findFlashcardSetsByAuthorId(UUID authorId) {
        return flashcardSetRepository.findAllByAuthorId(authorId);
    }

    /**
     * Retrieves all publicly available {@link FlashcardSet}.
     *
     * @return A {@link List} of {@link FlashcardSet}, empty if none found.
     */
    @Override
    public List<FlashcardSet> findPublicFlashcardSets() {
        return flashcardSetRepository.findByIsPublicIsTrue();
    }

    /**
     * Creates a new {@link FlashcardSet}.
     *
     * @param flashcardSet The {@link FlashcardSet} entity to create.
     * @return The persisted {@link FlashcardSet} with generated fields.
     */
    @Override
    public FlashcardSet createFlashcardSet(FlashcardSet flashcardSet) {
        return flashcardSetRepository.save(flashcardSet);
    }

    /**
     * Updates an existing {@link FlashcardSet}
     *
     * @param flashcardSet The {@link FlashcardSet} entity to create.
     * @return The updated {@link FlashcardSet} with generated fields.
     */
    @Override
    public FlashcardSet updateFlashcardSet(FlashcardSet flashcardSet) {
        return flashcardSetRepository.save(flashcardSet);
    }

    /**
     * Deletes a {@link FlashcardSet} by its {@link UUID}.
     *
     * @param id The {@link UUID} of the {@link FlashcardSet} to delete.
     */
    @Override
    public void deleteFlashcardSet(UUID id) {
        flashcardSetRepository.deleteById(id);
    }
}

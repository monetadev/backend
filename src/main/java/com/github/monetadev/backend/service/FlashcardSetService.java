package com.github.monetadev.backend.service;

import com.github.monetadev.backend.model.FlashcardSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlashcardSetService {
    /**
     * Retrieves a {@link FlashcardSet} by its unique identifier.
     * @param id The {@link UUID} of the {@link FlashcardSet} to find.
     * @return An {@link Optional} containing the found {@link FlashcardSet}, or empty if not found.
     */
    Optional<FlashcardSet> findFlashcardSetById(UUID id);

    /**
     * Retrieves all {@link FlashcardSet} objects created by a specific {@link com.github.monetadev.backend.model.User}.
     * @param authorId The {@link UUID} of the author {@link com.github.monetadev.backend.model.User}.
     * @return A {@link List} of {@link FlashcardSet}, empty if none found.
     */
    List<FlashcardSet> findFlashcardSetsByAuthorId(UUID authorId);

    /**
     * Retrieves all publicly available {@link FlashcardSet}.
     * @return A {@link List} of {@link FlashcardSet}, empty if none found.
     */
    List<FlashcardSet> findPublicFlashcardSets();

    /**
     * Creates a new {@link FlashcardSet}.
     * @param flashcardSet The {@link FlashcardSet} entity to create.
     * @return The persisted {@link FlashcardSet} with generated fields.
     */
    FlashcardSet createFlashcardSet(FlashcardSet flashcardSet);

    /**
     * Updates an existing {@link FlashcardSet}
     * @param flashcardSet The {@link FlashcardSet} entity to create.
     * @return The updated {@link FlashcardSet} with generated fields.
     */
    FlashcardSet updateFlashcardSet(FlashcardSet flashcardSet);

    /**
     * Deletes a {@link FlashcardSet} by its {@link UUID}.
     * @param id The {@link UUID} of the {@link FlashcardSet} to delete.
     */
    void deleteFlashcardSet(UUID id);
}

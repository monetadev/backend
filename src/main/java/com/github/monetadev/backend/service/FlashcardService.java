package com.github.monetadev.backend.service;

import com.github.monetadev.backend.model.Flashcard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlashcardService {
    /**
     * Retrieves a {@link Flashcard} by its {@link UUID}.
     * @param id The {@link UUID} of the {@link Flashcard} to find.
     * @return An {@link Optional} containing the found {@link Flashcard}, or empty if not found.
     */
    Optional<Flashcard> findFlashcardById(UUID id);

    /**
     * Retrieves all {@link Flashcard} objects associated with a specific {@link com.github.monetadev.backend.model.FlashcardSet}.
     * @param setId The {@link UUID} of the {@link com.github.monetadev.backend.model.FlashcardSet} to query for.
     * @return A {@link List} of {@link Flashcard} objects belonging to the specified {@link com.github.monetadev.backend.model.FlashcardSet}, may be empty.
     */
    List<Flashcard> findFlashcardsBySetId(UUID setId);

    /**
     * Creates a new {@link Flashcard}.
     * @param flashcard The {@link Flashcard} entity to persist.
     * @return The persisted {@link Flashcard} with generated fields.
     */
    Flashcard createFlashcard(Flashcard flashcard);

    /**
     * Updates an existing {@link Flashcard}.
     * @param flashcard The {@link Flashcard} entity to update.
     * @return The updated {@link Flashcard} with generated fields.
     */
    Flashcard updateFlashcard(Flashcard flashcard);

    /**
     * Deletes a {@link Flashcard} from the database.
     * @param flashcard The {@link Flashcard} entity to delete.
     */
    void deleteFlashcard(Flashcard flashcard);
}

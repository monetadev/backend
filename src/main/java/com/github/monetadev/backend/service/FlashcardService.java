package com.github.monetadev.backend.service;

import com.github.monetadev.backend.exception.FlashcardNotFoundException;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedFlashcard;
import com.github.monetadev.backend.model.Flashcard;

import java.util.List;
import java.util.UUID;

public interface FlashcardService {
    /**
     * Retrieves a {@link Flashcard} by its {@link UUID}.
     * @param id The {@link UUID} of the {@link Flashcard} to find.
     * @return The found {@link Flashcard}.
     * @throws FlashcardNotFoundException if no flashcard with the given ID exists.
     */
    Flashcard findFlashcardById(UUID id) throws FlashcardNotFoundException;

    /**
     * Retrieves all {@link Flashcard} objects associated with a specific {@link com.github.monetadev.backend.model.FlashcardSet}.
     * @param setId The {@link UUID} of the {@link com.github.monetadev.backend.model.FlashcardSet} to query for.
     * @return A {@link List} of {@link Flashcard} objects belonging to the specified {@link com.github.monetadev.backend.model.FlashcardSet}, may be empty.
     */
    PaginatedFlashcard findFlashcardsBySetId(UUID setId, int page, int size);

    /**
     * Creates a new {@link Flashcard}.
     * @param flashcard The {@link Flashcard} entity to persist.
     * @return The persisted {@link Flashcard} with generated fields.
     */
    Flashcard createFlashcard(Flashcard flashcard);

    /**
     * Updates an existing {@link Flashcard}.
     * @param flashcard The {@link Flashcard} entity to update.
     * @return The updated {@link Flashcard}.
     * @throws FlashcardNotFoundException if the flashcard to update does not exist.
     */
    Flashcard updateFlashcard(Flashcard flashcard) throws FlashcardNotFoundException;

    /**
     * Deletes a {@link Flashcard} from the database.
     * @param flashcard The {@link Flashcard} entity to delete.
     * @throws FlashcardNotFoundException if the flashcard to delete does not exist.
     */
    void deleteFlashcard(Flashcard flashcard) throws FlashcardNotFoundException;
}

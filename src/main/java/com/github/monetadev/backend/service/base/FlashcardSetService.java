package com.github.monetadev.backend.service.base;

import com.github.monetadev.backend.exception.FlashcardSetNotFoundException;
import com.github.monetadev.backend.graphql.type.input.FlashcardSetInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedFlashcardSet;
import com.github.monetadev.backend.model.FlashcardSet;

import java.util.List;
import java.util.UUID;

public interface FlashcardSetService {
    /**
     * Retrieves a {@link FlashcardSet} by its unique identifier.
     * @param id The {@link UUID} of the {@link FlashcardSet} to find.
     * @return The found {@link FlashcardSet}.
     * @throws FlashcardSetNotFoundException if no flashcard set with the given ID exists.
     */
    FlashcardSet findFlashcardSetById(UUID id) throws FlashcardSetNotFoundException;

    /**
     * Retrieves all {@link FlashcardSet} objects created by a specific {@link com.github.monetadev.backend.model.User}.
     * @param authorId The {@link UUID} of the author {@link com.github.monetadev.backend.model.User}.
     * @return A {@link List} of {@link FlashcardSet}, empty if none found.
     */
    PaginatedFlashcardSet findFlashcardSetsByAuthorId(UUID authorId, int page, int size);

    /**
     * Retrieves all publicly available {@link FlashcardSet}.
     * @return A {@link List} of {@link FlashcardSet}, empty if none found.
     */
    PaginatedFlashcardSet findPublicFlashcardSets(int page, int size);

    /**
     * Retrieves all {@link FlashcardSet} objects that match the given title and description.
     * @param query The title and description to search for. Case-insensitive.
     * @param page The page number to retrieve. Starts at 0.
     * @param size The number of items to retrieve per page. Must be greater than 0.
     * @return A {@link List} of {@link FlashcardSet} objects matching the given title and description, empty if none found.
     */
    PaginatedFlashcardSet findFlashcardSetsByLikeTitleOrDescription(String query, int page, int size);

    /**
     * Creates a new {@link FlashcardSet}.
     * @param flashcardSetInput The {@link FlashcardSetInput} DTO to persist.
     * @return The persisted {@link FlashcardSet} with generated fields.
     */
    FlashcardSet createFlashcardSet(FlashcardSetInput flashcardSetInput);

    /**
     * Updates an existing {@link FlashcardSet}
     * @param id The {@link UUID} of the flashcard set to update.
     * @param flashcardSetInput The {@link FlashcardSetInput} DTO to persist.
     * @return The updated {@link FlashcardSet}.
     * @throws FlashcardSetNotFoundException if the flashcard set to update does not exist.
     */
    FlashcardSet updateFlashcardSet(UUID id, FlashcardSetInput flashcardSetInput) throws FlashcardSetNotFoundException;

    /**
     * Deletes a {@link FlashcardSet} by its {@link UUID}.
     * @param id The {@link UUID} of the {@link FlashcardSet} to delete.
     * @return The title of the deleted flashcard set.
     * @throws FlashcardSetNotFoundException if no flashcard set with the given ID exists.
     */
    String deleteFlashcardSet(UUID id) throws FlashcardSetNotFoundException;
}

package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.exception.FlashcardSetNotFoundException;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.FlashcardSetRepository;
import com.github.monetadev.backend.service.AuthenticationService;
import com.github.monetadev.backend.service.FlashcardSetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FlashcardSetServiceImpl implements FlashcardSetService {
    private final FlashcardSetRepository flashcardSetRepository;
    private final AuthenticationService authenticationService;

    public FlashcardSetServiceImpl(FlashcardSetRepository flashcardSetRepository, AuthenticationService authenticationService) {
        this.flashcardSetRepository = flashcardSetRepository;
        this.authenticationService = authenticationService;
    }

    /**
     * Retrieves a {@link FlashcardSet} by its unique identifier.
     *
     * @param id The {@link UUID} of the {@link FlashcardSet} to find.
     * @return The found {@link FlashcardSet}.
     * @throws FlashcardSetNotFoundException if no flashcard set with the given ID exists.
     */
    @Override
    public FlashcardSet findFlashcardSetById(UUID id) throws FlashcardSetNotFoundException {
        return flashcardSetRepository.findById(id)
                .orElseThrow(() -> new FlashcardSetNotFoundException("Flashcard set not found with ID: " + id));
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
        flashcardSet.setAuthor(authenticationService.getAuthenticatedUser());
        return flashcardSetRepository.save(flashcardSet);
    }

    /**
     * Updates an existing {@link FlashcardSet}
     *
     * @param flashcardSet The {@link FlashcardSet} entity to create.
     * @return The updated {@link FlashcardSet}.
     * @throws FlashcardSetNotFoundException if the flashcard set to update does not exist.
     */
    @Override
    public FlashcardSet updateFlashcardSet(FlashcardSet flashcardSet) throws FlashcardSetNotFoundException {
        if (flashcardSet.getId() != null && !flashcardSetRepository.existsById(flashcardSet.getId())) {
            throw new FlashcardSetNotFoundException("Cannot update non-existent flashcard set with ID: " + flashcardSet.getId());
        }
        return flashcardSetRepository.save(flashcardSet);
    }

    /**
     * Deletes a {@link FlashcardSet} by its {@link UUID}.
     *
     * @param id The {@link UUID} of the {@link FlashcardSet} to delete.
     * @throws FlashcardSetNotFoundException if no flashcard set with the given ID exists.
     */
    @Override
    public void deleteFlashcardSet(UUID id) throws FlashcardSetNotFoundException {
        if (!flashcardSetRepository.existsById(id)) {
            throw new FlashcardSetNotFoundException("Cannot delete non-existent flashcard set with ID: " + id);
        }
        flashcardSetRepository.deleteById(id);
    }
}

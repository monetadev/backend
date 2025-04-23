package com.github.monetadev.backend.service.base.impl;

import com.github.monetadev.backend.exception.FlashcardSetNotFoundException;
import com.github.monetadev.backend.graphql.type.input.FlashcardInput;
import com.github.monetadev.backend.graphql.type.input.FlashcardSetInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedFlashcardSet;
import com.github.monetadev.backend.model.Flashcard;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.repository.FlashcardSetRepository;
import com.github.monetadev.backend.service.base.FlashcardSetService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
     * {@inheritDoc}
     */
    @Override
    public FlashcardSet findFlashcardSetById(UUID id) throws FlashcardSetNotFoundException {
        return flashcardSetRepository.findById(id)
                .orElseThrow(() -> new FlashcardSetNotFoundException("Flashcard set not found with ID: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedFlashcardSet findFlashcardSetsByAuthorId(UUID authorId, int page, int size) {
        Page<FlashcardSet> flashcardSets = flashcardSetRepository.findAllByAuthorId(authorId, PageRequest.of(page, size));

        return PaginatedFlashcardSet.of()
                .items(flashcardSets.getContent())
                .totalPages(flashcardSets.getTotalPages())
                .totalElements(flashcardSets.getTotalElements())
                .currentPage(flashcardSets.getNumber())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedFlashcardSet findPublicFlashcardSets(int page, int size) {
        Page<FlashcardSet> flashcardSets = flashcardSetRepository.findAllByIsPublic(true, PageRequest.of(page, size));

        return PaginatedFlashcardSet.of()
                .items(flashcardSets.getContent())
                .totalPages(flashcardSets.getTotalPages())
                .totalElements(flashcardSets.getTotalElements())
                .currentPage(flashcardSets.getNumber())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FlashcardSet createFlashcardSet(FlashcardSetInput flashcardSetInput) {
        FlashcardSet flashcardSet = new FlashcardSet();
        return mapAndPersistFlashcardSet(flashcardSetInput, flashcardSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FlashcardSet updateFlashcardSet(UUID id, FlashcardSetInput flashcardSetInput) throws FlashcardSetNotFoundException {
        FlashcardSet flashcardSet = flashcardSetRepository.findById(id)
                .orElseThrow(() -> new FlashcardSetNotFoundException("Couldn't find set with ID: " + id));
        return mapAndPersistFlashcardSet(flashcardSetInput, flashcardSet);
    }

    @NotNull
    private FlashcardSet mapAndPersistFlashcardSet(FlashcardSetInput flashcardSetInput, FlashcardSet flashcardSet) {
        flashcardSet.setAuthor(authenticationService.getAuthenticatedUser());
        flashcardSet.setTitle(flashcardSetInput.getTitle());
        flashcardSet.setDescription(flashcardSetInput.getDescription());
        flashcardSet.setIsPublic(flashcardSetInput.getIsPublic());

        Map<Integer, Flashcard> existingFlashcardsByPosition = new HashMap<>();
        if (flashcardSet.getFlashcards() != null) {
            for (Flashcard flashcard : flashcardSet.getFlashcards()) {
                existingFlashcardsByPosition.put(flashcard.getPosition(), flashcard);
            }
        }

        List<Flashcard> updatedFlashcards = new ArrayList<>();

        if (flashcardSetInput.getFlashcards() != null && !flashcardSetInput.getFlashcards().isEmpty()) {
            List<FlashcardInput> sortedInputs = new ArrayList<>(flashcardSetInput.getFlashcards());
            sortedInputs.sort(Comparator.comparing(FlashcardInput::getPosition));

            for (int i = 0; i < sortedInputs.size(); i++) {
                FlashcardInput input = sortedInputs.get(i);
                int normalizedPosition = i + 1;

                Flashcard flashcard;
                if (existingFlashcardsByPosition.containsKey(input.getPosition())) {
                    flashcard = existingFlashcardsByPosition.get(input.getPosition());
                } else {
                    flashcard = new Flashcard();
                    flashcard.setFlashcardSet(flashcardSet);
                }
                flashcard.setTerm(input.getTerm());
                flashcard.setDefinition(input.getDefinition());
                flashcard.setPosition(normalizedPosition);
                updatedFlashcards.add(flashcard);
            }
        }

        flashcardSet.setFlashcards(updatedFlashcards);
        return flashcardSetRepository.save(flashcardSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String deleteFlashcardSet(UUID id) throws FlashcardSetNotFoundException {
        if (!flashcardSetRepository.existsById(id)) {
            throw new FlashcardSetNotFoundException("Cannot delete non-existent flashcard set with ID: " + id);
        }
        String title = flashcardSetRepository.findById(id).get().getTitle();
        flashcardSetRepository.deleteById(id);
        return title;
    }
}

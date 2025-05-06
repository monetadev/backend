package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.graphql.type.input.FlashcardSetInput;
import com.github.monetadev.backend.graphql.type.filters.FlashcardSetFilterInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedFlashcardSet;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.service.base.FlashcardService;
import com.github.monetadev.backend.service.base.FlashcardSetService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

@DgsComponent
public class FlashcardSetController {
    private final FlashcardSetService flashcardSetService;
    private final FlashcardService flashcardService;

    public FlashcardSetController(FlashcardSetService flashcardSetService, FlashcardService flashcardService) {
        this.flashcardSetService = flashcardSetService;
        this.flashcardService = flashcardService;
    }

    @DgsQuery
    public FlashcardSet findFlashcardSetById(@InputArgument UUID id) {
        return flashcardSetService.findFlashcardSetById(id);
    }

    @DgsQuery
    public PaginatedFlashcardSet findFlashcardSetByAuthorId(@InputArgument UUID id,
                                                            @InputArgument Integer page,
                                                            @InputArgument Integer size) {
        return flashcardSetService.findFlashcardSetsByAuthorId(id, page, size);
    }

    @DgsQuery
    public PaginatedFlashcardSet findPublicFlashcardSets(@InputArgument FlashcardSetFilterInput filter,
                                                         @InputArgument Integer page,
                                                         @InputArgument Integer size) {
        return flashcardSetService.findPublicFlashcardSets(page, size);
    }

    @DgsQuery
    public PaginatedFlashcardSet searchPublicFlashcardSets(@InputArgument FlashcardSetFilterInput filter,
                                                     @InputArgument String query,
                                                     @InputArgument Integer page,
                                                     @InputArgument Integer size) {
        return flashcardSetService.findFlashcardSetsByLikeTitleOrDescription(query, page, size);
    }

    @DgsMutation
    public FlashcardSet createFlashcardSet(@InputArgument FlashcardSetInput flashcardSetInput) {
        return flashcardSetService.createFlashcardSet(flashcardSetInput);
    }

    @DgsMutation
    public FlashcardSet updateFlashcardSet(@InputArgument UUID id,
                                           @InputArgument FlashcardSetInput flashcardSetInput) {
        return flashcardSetService.updateFlashcardSet(id, flashcardSetInput);
    }

    @DgsMutation
    @PreAuthorize("hasAuthority('MANAGE_USER_FLASHCARD') or authentication.principal.userId == #userId")
    public String deleteFlashcardSet(@InputArgument UUID userId,
                                     @InputArgument UUID setId) {
        return flashcardSetService.deleteFlashcardSet(setId);
    }
}

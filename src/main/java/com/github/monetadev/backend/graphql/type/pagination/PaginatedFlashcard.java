package com.github.monetadev.backend.graphql.type.pagination;

import com.github.monetadev.backend.model.Flashcard;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaginatedFlashcard extends PaginatedResponse<Flashcard> {
    private PaginatedFlashcard(List<Flashcard> flashcards, int totalPages, long totalElements, int currentPage) {
        super(flashcards, totalPages, totalElements, currentPage);
    }

    public static Builder of() {
        return new Builder();
    }

    public static class Builder extends PaginatedResponseBuilder<Flashcard, PaginatedFlashcard, Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public PaginatedFlashcard build() {
            return new PaginatedFlashcard(items, totalPages, totalElements, currentPage);
        }
    }
}

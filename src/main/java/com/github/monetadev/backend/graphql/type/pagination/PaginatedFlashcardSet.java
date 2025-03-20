package com.github.monetadev.backend.graphql.type.pagination;

import com.github.monetadev.backend.model.FlashcardSet;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaginatedFlashcardSet extends PaginatedResponse<FlashcardSet> {
    private PaginatedFlashcardSet(List<FlashcardSet> flashcardSets, int totalPages, long totalElements, int currentPage) {
        super(flashcardSets, totalPages, totalElements, currentPage);
    }

    public static Builder of() {
        return new Builder();
    }

    public static class Builder extends PaginatedResponseBuilder<FlashcardSet, PaginatedFlashcardSet, Builder>  {
        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public PaginatedFlashcardSet build() {
            return new PaginatedFlashcardSet(items, totalPages, totalElements, currentPage);
        }
    }
}

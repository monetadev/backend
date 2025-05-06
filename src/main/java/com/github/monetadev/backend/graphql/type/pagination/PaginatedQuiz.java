package com.github.monetadev.backend.graphql.type.pagination;

import com.github.monetadev.backend.model.Quiz;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaginatedQuiz extends PaginatedResponse<Quiz> {
    private PaginatedQuiz(List<Quiz> items, int totalPages, long totalElements, int currentPage) {
        super(items, totalPages, totalElements, currentPage);
    }

    public static Builder of() {
        return new Builder();
    }

    public static class Builder extends PaginatedResponseBuilder<Quiz, PaginatedQuiz, Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public PaginatedQuiz build() {
            return new PaginatedQuiz(items, totalPages, totalElements, currentPage);
        }
    }
}

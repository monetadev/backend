package com.github.monetadev.backend.graphql.type.pagination;

import com.github.monetadev.backend.model.QuizAttempt;

import java.util.List;

public class PaginatedQuizAttempt extends PaginatedResponse<QuizAttempt> {
    private PaginatedQuizAttempt(List<QuizAttempt> items, int totalPages, long totalElements, int currentPage) {
        super(items, totalPages, totalElements, currentPage);
    }

    public static Builder of() {
        return new Builder();
    }


    public static class Builder extends PaginatedResponseBuilder<QuizAttempt, PaginatedQuizAttempt, Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public PaginatedQuizAttempt build() {
            return new PaginatedQuizAttempt(items, totalPages, totalElements, currentPage);
        }
    }
}

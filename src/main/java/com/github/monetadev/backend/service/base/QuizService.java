package com.github.monetadev.backend.service.base;

import com.github.monetadev.backend.graphql.type.ai.quiz.generate.GeneratedQuiz;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedQuiz;
import com.github.monetadev.backend.model.Quiz;

import java.util.UUID;

public interface QuizService {
    Quiz findQuizById(UUID id);
    Quiz saveQuiz(Quiz quiz);
    Quiz saveQuizFromInput(QuizInput quizInput);
    Quiz saveGeneratedQuiz(GeneratedQuiz generatedQuiz, UUID flashcardSetId);
    String deleteQuiz(UUID id);
    PaginatedQuiz getCurrentAuthenticatedUserQuizzes(int page, int size);
    PaginatedQuiz getFlashcardSetQuizzes(UUID setId, int page, int size);
    Integer calculateFlashcardSetAverageQuizScore(UUID setId);
    Integer calculateUserTotalAverageQuizScore(UUID userId);
}

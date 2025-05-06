package com.github.monetadev.backend.service.base;

import com.github.monetadev.backend.graphql.type.input.quiz.QuizInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedQuiz;
import com.github.monetadev.backend.model.Quiz;

import java.util.UUID;

public interface QuizService {
    Quiz saveQuiz(Quiz quiz);
    Quiz saveQuizFromInput(QuizInput quizInput);
    String deleteQuiz(UUID id);
    PaginatedQuiz getCurrentAuthenticatedUserQuizzes(int page, int size);
    PaginatedQuiz getFlashcardSetQuizzes(UUID setId, int page, int size);
    Integer calculateFlashcardSetAverageQuizScore(UUID setId);
    Integer calculateUserAverageQuizScore(UUID userId);
}

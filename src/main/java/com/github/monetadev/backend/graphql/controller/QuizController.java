package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.graphql.type.ai.quiz.generate.GeneratedQuiz;
import com.github.monetadev.backend.graphql.type.ai.quiz.grade.GradedQuiz;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizGenOptions;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedQuiz;
import com.github.monetadev.backend.model.Quiz;
import com.github.monetadev.backend.service.ai.QuizGenerationService;
import com.github.monetadev.backend.service.base.QuizService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

@DgsComponent
public class QuizController {
    private final QuizService quizService;
    private final QuizGenerationService quizGenerationService;

    public QuizController(QuizService quizService, QuizGenerationService quizGenerationService) {
        this.quizService = quizService;
        this.quizGenerationService = quizGenerationService;
    }

    @DgsMutation
    public GeneratedQuiz generateQuiz(@InputArgument QuizGenOptions options) {
        return quizGenerationService.generateQuiz(options);
    }

    @DgsMutation
    public GradedQuiz gradeQuiz(@InputArgument QuizInput quiz) {
        return quizGenerationService.gradeQuizFromInput(quiz);
    }

    @DgsMutation
    public Quiz persistQuiz(@InputArgument QuizInput gradedQuiz) {
        return quizService.saveQuizFromInput(gradedQuiz);
    }

    @DgsMutation
    @PreAuthorize("hasAuthority('MANAGE_USER') or authentication.principal.userId == #userId")
    public String deleteQuiz(@InputArgument UUID userId, @InputArgument UUID quizId) {
        return quizService.deleteQuiz(quizId);
    }

    @DgsQuery
    public PaginatedQuiz getMyQuizzes(@InputArgument Integer page, @InputArgument Integer size) {
        return quizService.getCurrentAuthenticatedUserQuizzes(page, size);
    }

    @DgsQuery
    public PaginatedQuiz getFlashcardSetQuizzes(@InputArgument UUID setId,
                                 @InputArgument Integer page,
                                 @InputArgument Integer size) {
        return quizService.getFlashcardSetQuizzes(setId, page, size);
    }

    @DgsQuery
    public Integer calculateSetAverageGrade(@InputArgument UUID setId) {
        return quizService.calculateFlashcardSetAverageQuizScore(setId);
    }

    @DgsQuery
    public Integer calculateUserAverageGrade(@InputArgument UUID userId) {
        return quizService.calculateUserAverageQuizScore(userId);
    }
}

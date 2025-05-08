package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.graphql.type.pagination.PaginatedQuizAttempt;
import com.github.monetadev.backend.model.QuizAttempt;
import com.github.monetadev.backend.service.base.QuizAttemptService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.UUID;

@DgsComponent
public class QuizAttemptController {
    private final QuizAttemptService quizAttemptService;

    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    @DgsQuery
    public QuizAttempt getQuizAttempt(@InputArgument UUID attemptId) {
        return quizAttemptService.getQuizAttempt(attemptId);
    }

    @DgsQuery
    public PaginatedQuizAttempt getUserQuizAttempts(@InputArgument UUID userId,
                                                  @InputArgument Integer page,
                                                  @InputArgument Integer size) {
        return quizAttemptService.getUserQuizAttempts(userId, page, size);
    }

    @DgsQuery
    public PaginatedQuizAttempt getQuizAttemptsByQuizId(@InputArgument UUID quizId,
                                                      @InputArgument Integer page,
                                                      @InputArgument Integer size) {
        return quizAttemptService.getQuizAttemptsByQuizId(quizId, page, size);
    }

    @DgsQuery
    public PaginatedQuizAttempt getUserQuizAttemptsByQuizId(@InputArgument UUID userId,
                                                           @InputArgument UUID quizId,
                                                           @InputArgument Integer page,
                                                           @InputArgument Integer size) {
        return quizAttemptService.getUserQuizAttemptsByQuizId(userId, quizId, page, size);
    }

    @DgsMutation
    public String deleteQuizAttempt(@InputArgument UUID attemptId) {
        return quizAttemptService.deleteQuizAttempt(attemptId);
    }
}

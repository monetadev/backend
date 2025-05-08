package com.github.monetadev.backend.service.base;

import com.github.monetadev.backend.graphql.type.ai.quiz.grade.GradedQuiz;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizAttemptInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedQuizAttempt;
import com.github.monetadev.backend.model.QuizAttempt;

import java.util.UUID;

public interface QuizAttemptService {
    /**
     * Create a quiz attempt from an {@link QuizAttemptInput}
     * @param quizAttemptInput The attempted quiz with user responses.
     * @return The created quiz attempt with a calculated score
     */
    QuizAttempt createQuizAttemptFromQuizAttemptInput(QuizAttemptInput quizAttemptInput);

    /**
     * Create a quiz attempt from a graded quiz
     * @param quizAttempt The graded quiz attempt with calculated scores and feedback
     * @return The saved quiz attempt
     */
    QuizAttempt createQuizAttemptFromGradedQuiz(GradedQuiz quizAttempt, UUID quizId);

    /**
     * Get a quiz attempt by ID
     * @param attemptId The ID of the quiz attempt
     * @return The quiz attempt
     */
    QuizAttempt getQuizAttempt(UUID attemptId);

    /**
     * Delete a quiz attempt
     * @param attemptId The ID of the quiz attempt to delete
     * @return The title of the deleted quiz attempt.
     */
    String deleteQuizAttempt(UUID attemptId);

    /**
     * Get paginated quiz attempts for a user
     * @param userId The ID of the user
     * @param page Page number (zero-based)
     * @param size Page size
     * @return Paginated quiz attempts
     */
    PaginatedQuizAttempt getUserQuizAttempts(UUID userId, int page, int size);

    /**
     * Get paginated quiz attempts for a quiz
     * @param quizId The ID of the quiz
     * @param page Page number (zero-based)
     * @param size Page size
     * @return Paginated quiz attempts
     */
    PaginatedQuizAttempt getQuizAttemptsByQuizId(UUID quizId, int page, int size);

    /**
     * Get paginated quiz attempts for a user and quiz
     * @param userId The ID of the user
     * @param quizId The ID of the quiz
     * @param page Page number (zero-based)
     * @param size Page size
     * @return Paginated quiz attempts
     */
    PaginatedQuizAttempt getUserQuizAttemptsByQuizId(UUID userId, UUID quizId, int page, int size);

    /**
     * Get the average score for a user across all quizzes
     * @param userId The ID of the user
     * @return The average score as a percentage
     */
    Double getAverageScoreForUser(UUID userId);

    /**
     * Get the average score for a quiz across all attempts
     * @param quizId The ID of the quiz
     * @return The average score as a percentage
     */
    Double getAverageScoreForQuiz(UUID quizId);

    /**
     * Get the average score for a user on a specific quiz
     * @param userId The ID of the user
     * @param quizId The ID of the quiz
     * @return The average score as a percentage
     */
    Double getAverageScoreForUserAndQuiz(UUID userId, UUID quizId);
}
package com.github.monetadev.backend.service.ai;

import com.github.monetadev.backend.graphql.type.input.quiz.QuizAttemptInput;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizGenOptions;
import com.github.monetadev.backend.model.Quiz;
import com.github.monetadev.backend.model.QuizAttempt;

public interface QuizGenerationService {
    Quiz generateQuiz(QuizGenOptions options);
    QuizAttempt gradeQuizFromInput(QuizAttemptInput quizInput);
}

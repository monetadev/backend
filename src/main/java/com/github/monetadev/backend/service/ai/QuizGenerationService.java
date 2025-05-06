package com.github.monetadev.backend.service.ai;

import com.github.monetadev.backend.graphql.type.ai.quiz.generate.GeneratedQuiz;
import com.github.monetadev.backend.graphql.type.ai.quiz.grade.GradedQuiz;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizGenOptions;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizInput;

public interface QuizGenerationService {
    GeneratedQuiz generateQuiz(QuizGenOptions options);
    GradedQuiz gradeQuizFromInput(QuizInput quizInput);
}

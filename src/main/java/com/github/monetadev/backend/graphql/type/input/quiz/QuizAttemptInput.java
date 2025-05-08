package com.github.monetadev.backend.graphql.type.input.quiz;

import com.github.monetadev.backend.model.QuizAttemptUserQuestionResponse;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@ToString
public class QuizAttemptInput {
    UUID quizId;
    List<QuestionInput> responses;
}

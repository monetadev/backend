package com.github.monetadev.backend.graphql.type.input.quiz;

import com.github.monetadev.backend.model.Question;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuizGenOptions {
    Integer kQuestions;
    UUID setId;
    List<Question.QuestionType> questionTypes;
}

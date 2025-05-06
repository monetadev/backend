package com.github.monetadev.backend.graphql.type.input.quiz;

import com.github.monetadev.backend.model.Question;
import lombok.Data;

import java.util.List;

@Data
public class QuestionInput {
    String content;
    Integer position;
    Question.QuestionType type;
    String userResponse;
    boolean isCorrectUserResponse;
    String feedback;
    List<OptionInput> options;
}

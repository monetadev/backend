package com.github.monetadev.backend.graphql.type.input.quiz;

import com.github.monetadev.backend.model.Question;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class QuestionInput {
    Integer position;
    String content;
    Question.QuestionType questionType;
    List<OptionInput> options;
}

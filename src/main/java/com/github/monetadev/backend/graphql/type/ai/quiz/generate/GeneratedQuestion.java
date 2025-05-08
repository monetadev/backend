package com.github.monetadev.backend.graphql.type.ai.quiz.generate;


import com.github.monetadev.backend.model.Question;
import lombok.Data;

import java.util.List;

@Data
public class GeneratedQuestion {
    String content;
    Integer position;
    String answer;
    Question.QuestionType questionType;
    List<GeneratedOption> options;
}

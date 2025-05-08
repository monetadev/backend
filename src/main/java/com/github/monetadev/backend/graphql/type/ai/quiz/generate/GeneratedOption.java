package com.github.monetadev.backend.graphql.type.ai.quiz.generate;

import lombok.Data;

@Data
public class GeneratedOption {
    String content;
    Integer position;
    Boolean isCorrect;
}

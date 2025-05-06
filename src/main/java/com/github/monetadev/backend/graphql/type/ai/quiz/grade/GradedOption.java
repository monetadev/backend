package com.github.monetadev.backend.graphql.type.ai.quiz.grade;

import lombok.Data;

@Data
public class GradedOption {
    String content;
    Integer position;
    boolean isCorrect;
}

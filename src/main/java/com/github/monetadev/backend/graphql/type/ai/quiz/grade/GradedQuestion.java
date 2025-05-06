package com.github.monetadev.backend.graphql.type.ai.quiz.grade;

import lombok.Data;

import java.util.List;

@Data
public class GradedQuestion {
    String content;
    Integer position;
    String userResponse;
    boolean isCorrectAnswer;
    String feedback;
    List<GradedOption> options;
}

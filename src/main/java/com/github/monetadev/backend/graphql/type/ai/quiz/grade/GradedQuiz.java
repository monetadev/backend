package com.github.monetadev.backend.graphql.type.ai.quiz.grade;

import lombok.Data;

import java.util.List;

@Data
public class GradedQuiz {
    String title;
    String description;
    List<GradedQuestion> questions;
}

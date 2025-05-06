package com.github.monetadev.backend.graphql.type.ai.quiz.generate;

import lombok.Data;

import java.util.List;

@Data
public class GeneratedQuiz {
    String title;
    String description;
    List<GeneratedQuestion> questions;
}

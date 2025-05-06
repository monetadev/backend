package com.github.monetadev.backend.graphql.type.input.quiz;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuizInput {
    String title;
    String description;
    List<QuestionInput> questions;
    UUID setId;
}

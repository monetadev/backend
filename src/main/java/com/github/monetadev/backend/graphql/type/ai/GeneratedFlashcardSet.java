package com.github.monetadev.backend.graphql.type.ai;

import lombok.Data;

import java.util.List;

@Data
public class GeneratedFlashcardSet {
    String title;
    String description;
    List<GeneratedFlashcard> generatedFlashcards;
}

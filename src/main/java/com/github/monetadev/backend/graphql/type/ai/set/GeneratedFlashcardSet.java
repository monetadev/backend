package com.github.monetadev.backend.graphql.type.ai.set;

import lombok.Data;

import java.util.List;

@Data
public class GeneratedFlashcardSet {
    String title;
    String description;
    List<GeneratedFlashcard> generatedFlashcards;
}

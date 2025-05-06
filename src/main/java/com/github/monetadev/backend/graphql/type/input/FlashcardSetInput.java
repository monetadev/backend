package com.github.monetadev.backend.graphql.type.input;

import lombok.Data;

import java.util.List;

@Data
public class FlashcardSetInput {
    String title;
    String description;
    Boolean isPublic;
    List<FlashcardInput> flashcards;
}

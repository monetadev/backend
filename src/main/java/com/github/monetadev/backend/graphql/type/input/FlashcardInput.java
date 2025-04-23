package com.github.monetadev.backend.graphql.type.input;

import lombok.Data;

@Data
public class FlashcardInput {
    String term;
    String definition;
    Integer position;
}

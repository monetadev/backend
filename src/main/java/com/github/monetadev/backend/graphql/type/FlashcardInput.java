package com.github.monetadev.backend.graphql.type;

import lombok.Data;

@Data
public class FlashcardInput {
    String term;
    String definition;
    Integer position;
}

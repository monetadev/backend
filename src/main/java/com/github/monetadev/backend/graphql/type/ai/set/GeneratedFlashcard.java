package com.github.monetadev.backend.graphql.type.ai.set;

import lombok.Data;

@Data
public class GeneratedFlashcard {
    String term;
    String definition;
    Integer position;
}

package com.github.monetadev.backend.graphql.type.ai;

import lombok.Data;

@Data
public class GeneratedFlashcard {
    String term;
    String definition;
    Integer position;
}

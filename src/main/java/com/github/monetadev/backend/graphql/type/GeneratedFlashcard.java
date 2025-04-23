package com.github.monetadev.backend.graphql.type;

import lombok.Data;

@Data
public class GeneratedFlashcard {
    String term;
    String definition;
    Integer position;
}

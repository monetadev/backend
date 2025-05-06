package com.github.monetadev.backend.graphql.type.input.quiz;

import lombok.Data;

@Data
public class OptionInput {
    String content;
    Integer position;
    Boolean isCorrect;
}

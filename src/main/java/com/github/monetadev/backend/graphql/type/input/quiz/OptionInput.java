package com.github.monetadev.backend.graphql.type.input.quiz;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OptionInput {
    String content;
    Integer position;
    Boolean isCorrect;
}

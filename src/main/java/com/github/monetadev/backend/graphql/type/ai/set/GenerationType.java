package com.github.monetadev.backend.graphql.type.ai.set;

import lombok.ToString;

@ToString(of = "label")
public enum GenerationType {
    BRIEF("Concise"),
    VERBOSE("Verbose");

    private final String label;

    GenerationType(String label) {
        this.label = label;
    }
}

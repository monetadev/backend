package com.github.monetadev.backend.graphql.type.filters;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class FlashcardSetFilterInput {
    String searchTerm;
    Boolean isPublic;
    OffsetDateTime createdBefore;
    OffsetDateTime createdAfter;
}

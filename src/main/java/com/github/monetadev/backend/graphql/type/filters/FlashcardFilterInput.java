package com.github.monetadev.backend.graphql.type.filters;

import java.time.OffsetDateTime;

public class FlashcardFilterInput {
    String searchTerm;
    OffsetDateTime createdBefore;
    OffsetDateTime createdAfter;
}

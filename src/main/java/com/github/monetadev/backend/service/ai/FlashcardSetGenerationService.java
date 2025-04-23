package com.github.monetadev.backend.service.ai;

import com.github.monetadev.backend.graphql.type.FlashcardGenOptions;
import com.github.monetadev.backend.graphql.type.GeneratedFlashcardSet;

public interface FlashcardSetGenerationService {
    GeneratedFlashcardSet generateFlashcardSet(FlashcardGenOptions options);
}

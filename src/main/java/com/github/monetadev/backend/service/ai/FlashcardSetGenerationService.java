package com.github.monetadev.backend.service.ai;

import com.github.monetadev.backend.graphql.type.ai.FlashcardGenOptions;
import com.github.monetadev.backend.graphql.type.ai.GeneratedFlashcardSet;

public interface FlashcardSetGenerationService {
    GeneratedFlashcardSet generateFlashcardSet(FlashcardGenOptions options);
}

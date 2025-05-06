package com.github.monetadev.backend.service.ai;

import com.github.monetadev.backend.graphql.type.ai.set.FlashcardGenOptions;
import com.github.monetadev.backend.graphql.type.ai.set.GeneratedFlashcardSet;

public interface FlashcardSetGenerationService {
    GeneratedFlashcardSet generateFlashcardSet(FlashcardGenOptions options);
}

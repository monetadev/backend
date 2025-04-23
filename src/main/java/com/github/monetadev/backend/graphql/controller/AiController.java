package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.graphql.type.ai.FlashcardGenOptions;
import com.github.monetadev.backend.graphql.type.ai.GeneratedFlashcardSet;
import com.github.monetadev.backend.service.ai.ChatAgentService;
import com.github.monetadev.backend.service.ai.FlashcardSetGenerationService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsSubscription;
import com.netflix.graphql.dgs.InputArgument;
import reactor.core.publisher.Flux;

import java.util.UUID;

@DgsComponent
public class AiController {
    private final ChatAgentService chatAgentService;
    private final FlashcardSetGenerationService flashcardSetGenerationService;

    public AiController(ChatAgentService chatAgentService, FlashcardSetGenerationService flashcardSetGenerationService) {
        this.chatAgentService = chatAgentService;
        this.flashcardSetGenerationService = flashcardSetGenerationService;
    }

    @DgsSubscription
    public Flux<String> flashcardSetChat(@InputArgument UUID conversationId,
                                         @InputArgument UUID setId,
                                         @InputArgument String message) {
        return chatAgentService.chat(conversationId, setId, message);
    }

    @DgsMutation
    public GeneratedFlashcardSet generateFlashcardSet(@InputArgument FlashcardGenOptions options) {
        return flashcardSetGenerationService.generateFlashcardSet(options);
    }
}

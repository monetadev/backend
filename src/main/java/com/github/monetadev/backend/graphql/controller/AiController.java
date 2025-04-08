package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.service.ai.ChatAgentService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsSubscription;
import com.netflix.graphql.dgs.InputArgument;
import reactor.core.publisher.Flux;

import java.util.UUID;

@DgsComponent
public class AiController {
    private final ChatAgentService chatAgentService;

    public AiController(ChatAgentService chatAgentService) {
        this.chatAgentService = chatAgentService;
    }

    @DgsSubscription
    public Flux<String> flashcardSetChat(@InputArgument UUID conversationId,
                                         @InputArgument UUID setId,
                                         @InputArgument String message) {
        return chatAgentService.chat(conversationId, setId, message);
    }
}

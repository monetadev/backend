package com.github.monetadev.backend.service.ai;

import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ChatAgentService {
    Flux<String> chat(UUID conversationId, UUID setId, String message);
}

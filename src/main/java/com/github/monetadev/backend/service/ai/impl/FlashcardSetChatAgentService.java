package com.github.monetadev.backend.service.ai.impl;

import com.github.monetadev.backend.model.Flashcard;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.service.ai.ChatAgentService;
import com.github.monetadev.backend.service.base.FlashcardSetService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
@Transactional
public class FlashcardSetChatAgentService implements ChatAgentService {
    private final ChatClient.Builder builder;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;
    private ChatClient chatClient;
    private final AuthenticationService authenticationService;
    private String systemPrompt;
    private final FlashcardSetService flashcardSetService;

    @Autowired
    public FlashcardSetChatAgentService(ChatClient.Builder builder,
                                        VectorStore vectorStore,
                                        ChatMemory chatMemory,
                                        @Value("classpath:ai/flashcard-set-agent/system.prompt") Resource systemPrompt,
                                        AuthenticationService authenticationService,
                                        FlashcardSetService flashcardSetService) {
        this.builder = builder;
        this.vectorStore = vectorStore;
        this.chatMemory = chatMemory;
        try {
            this.systemPrompt = systemPrompt.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
        this.authenticationService = authenticationService;
        this.flashcardSetService = flashcardSetService;
    }

    private void initializeChat(UUID setId) {
        User user = authenticationService.getAuthenticatedUser();
        FlashcardSet flashcardSet = flashcardSetService.findFlashcardSetById(setId);

        Map<String, String> values = new HashMap<>();
        values.put("secret", UUID.randomUUID().toString());
        values.put("username", user.getUsername());
        values.put("first_name", user.getFirstName());
        values.put("set_title", flashcardSet.getTitle());
        values.put("description", flashcardSet.getDescription());

        String flashcardTemplate =
                        """
                        ### FLASHCARD POSITION #$#MONETA#$#position#$#MONETA#$#
                        ###TERM####$#MONETA#$#term#$#MONETA#$#
                        ###DEFINITION####$#MONETA#$#definition#$#MONETA#$#
                        """;
        List<String> flashcards = new ArrayList<>();
        for (Flashcard flashcard : flashcardSet.getFlashcards()) {
            Map<String, String> flashcardValues = new HashMap<>();
            flashcardValues.put("position", flashcard.getPosition().toString());
            flashcardValues.put("term", flashcard.getTerm());
            flashcardValues.put("definition", flashcard.getDefinition());

            StringSubstitutor substitutor = new StringSubstitutor(flashcardValues, "#$#MONETA#$#", "#$#MONETA#$#");
            flashcards.add(substitutor.replace(flashcardTemplate));
        }
        values.put("flashcards", String.join(",", flashcards));

        StringSubstitutor substitutor = new StringSubstitutor(values, "#$#MONETA#$#", "#$#MONETA#$#");
        this.chatClient = builder
                .defaultSystem(substitutor.replace(systemPrompt))
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(vectorStore)
                )
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<String> chat(UUID conversationId, UUID setId, String message) {
        if (chatMemory.get(conversationId.toString(), 1).isEmpty()) {
            initializeChat(setId);
        }
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId.toString())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream().content();
    }
}

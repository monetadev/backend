package com.github.monetadev.backend.service.ai.impl;

import com.github.monetadev.backend.model.Flashcard;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.service.ai.ChatAgentService;
import com.github.monetadev.backend.service.base.FlashcardSetService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.*;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
@Transactional
public class FlashcardSetChatAgentService implements ChatAgentService {
    private final ChatMemory chatMemory;
    private final PromptTemplate flashcardPrompt;
    private final PromptTemplate metadataPrompt;
    private final PromptTemplate userPrompt;
    private final ChatClient chatClient;
    private final AuthenticationService authenticationService;
    private final FlashcardSetService flashcardSetService;

    @Autowired
    public FlashcardSetChatAgentService(ChatClient.Builder chatClientBuilder,
                                        VectorStore vectorStore,
                                        ChatMemory chatMemory,
                                        AuthenticationService authenticationService,
                                        FlashcardSetService flashcardSetService,
                                        @Value("classpath:ai/set/agent/system.st") Resource systemPrompt,
                                        @Value("classpath:ai/set/agent/flashcard.st") Resource flashcardPrompt,
                                        @Value("classpath:ai/set/agent/metadata.st") Resource metadataPrompt,
                                        @Value("classpath:ai/set/agent/user.st") Resource userPrompt) {
        this.chatMemory = chatMemory;
        this.authenticationService = authenticationService;
        this.flashcardSetService = flashcardSetService;

        this.flashcardPrompt = new PromptTemplate(flashcardPrompt);
        this.metadataPrompt = new PromptTemplate(metadataPrompt);
        this.userPrompt = new PromptTemplate(userPrompt);


        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .defaultOptions(ChatOptions.builder()
                        .temperature(0.0)
                        .topP(0.8)
                        .build())
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        RetrievalAugmentationAdvisor.builder()
                                .documentRetriever(VectorStoreDocumentRetriever.builder() // TODO: Add filtering.
                                        .vectorStore(vectorStore)
                                        .similarityThreshold(0.6)
                                        .topK(6)
                                        .build())
                                .queryAugmenter(ContextualQueryAugmenter.builder()
                                        .allowEmptyContext(true)
                                        .build())
                                .build()
                ).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<String> chat(UUID conversationId, UUID setId, String message) {
        if (chatMemory.get(conversationId.toString(), 1).isEmpty()) {
            return initialChat(conversationId, setId, message);
        }
        return sendMessage(conversationId, message);
    }

    private Flux<String> initialChat(UUID conversationId, UUID setId, String message) {
        User user = authenticationService.getAuthenticatedUser();
        FlashcardSet flashcardSet = flashcardSetService.findFlashcardSetById(setId);
        // Metadata
        Message metadataMessage = metadataPrompt.createMessage(Map.of(
                "username", user.getUsername(),
                "first_name", user.getFirstName(),
                "title", flashcardSet.getTitle(),
                "description", flashcardSet.getDescription()
        ));

        // Flashcard content
        List<String> formattedFlashcards = new ArrayList<>();
        for (Flashcard flashcard : flashcardSet.getFlashcards()) {
            Map<String, Object> flashcardMap = Map.of(
                    "position", flashcard.getPosition().toString(),
                    "term", flashcard.getTerm(),
                    "definition", flashcard.getDefinition()
            );
            formattedFlashcards.add(flashcardPrompt.render(flashcardMap));
        }
        // User message
        Message userMessage = userPrompt.createMessage(Map.of(
                "metadata", metadataMessage.getText(),
                "flashcard", String.join(System.lineSeparator(), formattedFlashcards),
                "message", message
        ));

        return sendMessage(conversationId, userMessage.getText());
    }

    private Flux<String> sendMessage(UUID conversationId, String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(
                        a -> a
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId.toString())
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                )
                .stream().content();
    }
}

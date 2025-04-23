package com.github.monetadev.backend.service.ai.impl;

import com.github.monetadev.backend.graphql.type.FlashcardGenOptions;
import com.github.monetadev.backend.graphql.type.GeneratedFlashcardSet;
import com.github.monetadev.backend.service.ai.FlashcardSetGenerationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FlashcardSetGenerationServiceImpl implements FlashcardSetGenerationService {

    private final ChatClient.Builder chatClientBuilder;
    private final Resource systemPrompt;
    private final Resource rewritePrompt;
    private final Resource emptyContextPrompt;
    private final QdrantVectorStore vectorStore;

    public FlashcardSetGenerationServiceImpl(ChatClient.Builder chatClientBuilder,
                                             QdrantVectorStore vectorStore,
                                             @Value("classpath:ai/flashcard-gen-agent/system.st") Resource systemPrompt,
                                             @Value("classpath:ai/flashcard-gen-agent/rewrite.st") Resource rewritePrompt,
                                             @Value("classpath:ai/flashcard-gen-agent/empty-context.st") Resource emptyContextPrompt) {
        this.chatClientBuilder = chatClientBuilder;
        this.vectorStore = vectorStore;
        this.systemPrompt = systemPrompt;
        this.rewritePrompt = rewritePrompt;
        this.emptyContextPrompt = emptyContextPrompt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneratedFlashcardSet generateFlashcardSet(FlashcardGenOptions options) {
        PromptTemplate systemPrompt = new PromptTemplate(this.systemPrompt);
        ChatClient chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.createMessage(Map.of(
                        "k", options.getKQuestions())
                        ).getText()
                )
                .defaultOptions(OpenAiChatOptions.builder()
                        .temperature(0.0)
                        .topP(0.9)
                        .build())
                .defaultAdvisors(RetrievalAugmentationAdvisor.builder()
                        .documentRetriever(VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .similarityThreshold(0.6)
                                .topK(10)
                                .build())
                        .queryAugmenter(ContextualQueryAugmenter.builder()
                                .allowEmptyContext(true)
                                .emptyContextPromptTemplate(new PromptTemplate(emptyContextPrompt))
                                .build())
                        .build())
                .build();

        PromptTemplate rewritePrompt = new PromptTemplate(this.rewritePrompt);
        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder.build().mutate())
                .promptTemplate(rewritePrompt)
                .build();

        Query query = queryTransformer.transform(new Query(options.getQuery()));
        // Handling for strange errors occurring. Needs further investigation.
        Message handling = new PromptTemplate(query.text()).createMessage(Map.of(
                "cmd", "generate"
        ));
        System.out.println(handling.getText());
        return chatClient
            .prompt()
                .options(OpenAiChatOptions.builder()
                        .temperature(0.4)
                        .topP(0.8)
                        .responseFormat(ResponseFormat.builder()
                                .type(ResponseFormat.Type.JSON_SCHEMA)
                                .jsonSchema("""
                                        {
                                           "type": "object",
                                           "properties": {
                                             "title": {
                                               "type": "string",
                                               "description": "The title of the flashcard set"
                                             },
                                             "description": {
                                               "type": "string",
                                               "description": "A description of the flashcard set's content"
                                             },
                                             "generatedFlashcards": {
                                               "type": "array",
                                               "items": {
                                                 "type": "object",
                                                 "properties": {
                                                   "term": {
                                                     "type": "string",
                                                     "description": "The term or concept"
                                                   },
                                                   "definition": {
                                                     "type": "string",
                                                     "description": "The explanation"
                                                   },
                                                   "position": {
                                                     "type": "integer",
                                                     "description": "The position in sequence"
                                                   }
                                                 },
                                                 "required": ["term", "definition", "position"],
                                                 "additionalProperties": false
                                               }
                                             }
                                           },
                                           "required": ["title", "description", "generatedFlashcards"],
                                           "additionalProperties": false
                                         }
                                        """)
                                .build())
                        .build())
                .user(handling.getText())
                .call()
                .entity(GeneratedFlashcardSet.class);
    }
}

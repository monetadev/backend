package com.github.monetadev.backend.service.ai.impl;

import com.github.monetadev.backend.graphql.type.ai.set.FlashcardGenOptions;
import com.github.monetadev.backend.graphql.type.ai.set.GeneratedFlashcardSet;
import com.github.monetadev.backend.service.ai.FlashcardSetGenerationService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlashcardSetGenerationServiceImpl implements FlashcardSetGenerationService {
    // AI dependencies.
    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;
    private final AuthenticationService authenticationService;

    // Vector prompts.
    private final Resource vectorSystemPrompt;
    private final Resource vectorRewritePrompt;

    // Document prompts.
    private final Resource documentRewritePrompt;
    private final Resource documentSystemPrompt;

    private final static String GENERATED_JSON_SCHEMA = """
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
                         "description": "The term, typically brief/one word."
                       },
                       "definition": {
                         "type": "string",
                         "description": "The explanation, typically verbose."
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
            """;
    private final ChatModel chatModel;

    public FlashcardSetGenerationServiceImpl(ChatClient.Builder chatClientBuilder,
                                             PgVectorStore vectorStore,
                                             AuthenticationService authenticationService,
                                             @Value("classpath:ai/gen/set/vector/system.st") Resource vectorSystemPrompt,
                                             @Value("classpath:ai/gen/set/vector/rewrite.st") Resource vectorRewritePrompt,
                                             @Value("classpath:ai/gen/set/document/rewrite.st") Resource documentRewritePrompt,
                                             @Value("classpath:ai/gen/set/document/system.st") Resource documentSystemPrompt, @Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatClientBuilder = chatClientBuilder;
        this.vectorStore = vectorStore;
        this.authenticationService = authenticationService;

        this.vectorSystemPrompt = vectorSystemPrompt;
        this.vectorRewritePrompt = vectorRewritePrompt;

        this.documentRewritePrompt = documentRewritePrompt;
        this.documentSystemPrompt = documentSystemPrompt;
        this.chatModel = chatModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneratedFlashcardSet generateFlashcardSet(FlashcardGenOptions options) {
        if (options.getReferenceFile() == null) {
            return generateVectorSupportedFlashcardSet(options);
        }
        return generateDocumentSupportedFlashcardSet(options);
    }

    private GeneratedFlashcardSet generateDocumentSupportedFlashcardSet(FlashcardGenOptions options) {
        PromptTemplate rewriteSystemPrompt = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('¶').endDelimiterToken('¶').build())
                .resource(documentRewritePrompt)
                .variables(Map.of(
                        "type", options.getGenerationType()
                ))
                .build();
        PromptTemplate systemPrompt = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('¶').endDelimiterToken('¶').build())
                .resource(documentSystemPrompt)
                .variables(Map.of(
                        "k", options.getKQuestions(),
                        "type", options.getGenerationType()
                ))
                .build();
        PromptTemplate userRewritePrompt = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('¶').endDelimiterToken('¶').build())
                .template("|#!#|MONETADEV|#!#|¶query¶|#!#|MONETADEV|#!#|¶document¶")
                .build();

        ChatClient chatClient = chatClientBuilder
                .defaultSystem(rewriteSystemPrompt.render())
                .defaultOptions(OpenAiChatOptions.builder()
                        .temperature(0.5)
                        .topP(0.45)
                        .build())
                .build()
                .mutate().build();

        List<Document> extractedDocuments = new TikaDocumentReader(
                options.getReferenceFile().getResource()).get();
        extractedDocuments = TokenTextSplitter.builder()
                .withChunkSize(1024)
                .withMaxNumChunks(128)
                .withKeepSeparator(true)
                .withMinChunkSizeChars(32)
                .build()
                .apply(extractedDocuments);
        extractedDocuments.forEach(document -> System.out.println(document.getFormattedContent()));

        String documents = chatClient
                .prompt(rewriteSystemPrompt.render())
                .options(OpenAiChatOptions.builder()
                        .model("gpt-4.1-nano")
                        .temperature(0.0)
                        .topP(0.4)
                        .build())
                .user(userRewritePrompt.render(Map.of(
                        "query", options.getQuery(),
                        "document", extractedDocuments.stream()
                                .map(Document::getFormattedContent)
                                .collect(Collectors.joining("\n---\n"))
                )))
                .call()
                .content();

        return chatClient
                .prompt(systemPrompt.render())
                .options(OpenAiChatOptions.builder()
                        .responseFormat(ResponseFormat.builder()
                                .type(ResponseFormat.Type.JSON_SCHEMA)
                                .jsonSchema(GENERATED_JSON_SCHEMA)
                                .build())
                        .build())
                .user(userRewritePrompt.render(Map.of(
                        "query", options.getQuery(),
                        "document", documents
                )))
                .call()
                .entity(GeneratedFlashcardSet.class);
    }

    private GeneratedFlashcardSet generateVectorSupportedFlashcardSet(FlashcardGenOptions options) {
        PromptTemplate rewriteSystemPrompt = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('¶').endDelimiterToken('¶').build())
                .resource(vectorRewritePrompt)
                .variables(Map.of(
                        "k", options.getKQuestions(),
                        "type", options.getGenerationType()
                ))
                .build();
        PromptTemplate systemPrompt = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('¶').endDelimiterToken('¶').build())
                .resource(vectorSystemPrompt)
                .variables(Map.of(
                        "k", options.getKQuestions(),
                        "type", options.getGenerationType()
                ))
                .build();
        PromptTemplate userRewritePrompt = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('¶').endDelimiterToken('¶').build())
                .template("|#!#|MONETADEV|#!#|¶query¶|#!#|MONETADEV|#!#|¶document¶")
                .build();

        ChatClient chatClient = chatClientBuilder
                .defaultSystem(rewriteSystemPrompt.render())
                .defaultOptions(OpenAiChatOptions.builder()
                        .temperature(0.7)
                        .topP(0.85)
                        .build())
                .build()
                .mutate().build();

        String retrieveQuery = chatClient
                .prompt()
                .options(OpenAiChatOptions.builder()
                        .model("gpt-4.1")
                        .temperature(0.3)
                        .topP(0.75)
                        .build())
                .user(options.getQuery())
                .call()
                .content();
        assert (retrieveQuery != null);

        DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(.75)
                .topK(8)
                .filterExpression(new FilterExpressionBuilder()
                        .eq("userId", authenticationService.getAuthenticatedUser().getId().toString())
                        .build())
                .build();

        List<Document> similarContent = retriever.retrieve(new Query(retrieveQuery));

        return chatClient
                .prompt(systemPrompt.render())
                .options(OpenAiChatOptions.builder()
                        .responseFormat(ResponseFormat.builder()
                                .type(ResponseFormat.Type.JSON_SCHEMA)
                                .jsonSchema(GENERATED_JSON_SCHEMA)
                                .build())
                        .build())
                .user(userRewritePrompt.render(Map.of(
                        "query", options.getQuery(),
                        "document", similarContent.stream()
                )))
                .call()
                .entity(GeneratedFlashcardSet.class);
    }
}

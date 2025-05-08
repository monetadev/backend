package com.github.monetadev.backend.service.ai.impl;

import com.github.monetadev.backend.graphql.type.ai.quiz.generate.GeneratedQuiz;
import com.github.monetadev.backend.graphql.type.ai.quiz.grade.GradedQuiz;
import com.github.monetadev.backend.graphql.type.input.quiz.*;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.model.Quiz;
import com.github.monetadev.backend.model.QuizAttempt;
import com.github.monetadev.backend.repository.QuizRepository;
import com.github.monetadev.backend.service.ai.QuizGenerationService;
import com.github.monetadev.backend.service.base.FlashcardSetService;
import com.github.monetadev.backend.service.base.QuizAttemptService;
import com.github.monetadev.backend.service.base.QuizService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizGenerationServiceImpl implements QuizGenerationService {
    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;

    private final String GENERATED_QUIZ_SCHEMA;
    private final String GRADED_QUIZ_SCHEMA;

    private final AuthenticationService authenticationService;
    private final Resource generationSystemPrompt;
    private final Resource generationUserPrompt;
    private final Resource gradeSystemPrompt;
    private final Resource gradeUserPrompt;
    private final FlashcardSetService flashcardSetService;
    private final QuizService quizService;
    private final QuizRepository quizRepository;
    private final QuizAttemptService quizAttemptService;

    public QuizGenerationServiceImpl(ChatClient.Builder chatClientBuilder,
                                     VectorStore vectorStore,
                                     AuthenticationService authenticationService,
                                     @Value("classpath:ai/quiz/gen/schema.json") Resource generatedQuizSchema,
                                     @Value("classpath:ai/quiz/gen/system.st") Resource generationSystemPrompt,
                                     @Value("classpath:ai/quiz/gen/user.st") Resource generationUserPrompt,
                                     @Value("classpath:ai/quiz/grade/schema.json") Resource gradedQuizSchema,
                                     @Value("classpath:ai/quiz/grade/system.st") Resource gradeSystemPrompt,
                                     @Value("classpath:ai/quiz/grade/user.st") Resource gradeUserPrompt,
                                     FlashcardSetService flashcardSetService,
                                     QuizService quizService, QuizRepository quizRepository, QuizAttemptService quizAttemptService) {
        this.chatClientBuilder = chatClientBuilder;
        this.vectorStore = vectorStore;
        this.authenticationService = authenticationService;
        this.generationSystemPrompt = generationSystemPrompt;
        this.generationUserPrompt = generationUserPrompt;
        this.gradeSystemPrompt = gradeSystemPrompt;
        this.gradeUserPrompt = gradeUserPrompt;
        this.flashcardSetService = flashcardSetService;
        this.quizService = quizService;

        try {
            GENERATED_QUIZ_SCHEMA = generatedQuizSchema.getContentAsString(Charset.defaultCharset());
            GRADED_QUIZ_SCHEMA = gradedQuizSchema.getContentAsString(Charset.defaultCharset());
        } catch (IOException ignored) {
            throw new RuntimeException("Failed to load quiz schema.");
        }
        this.quizRepository = quizRepository;
        this.quizAttemptService = quizAttemptService;
    }

    public Quiz generateQuiz(QuizGenOptions options) {
        // TODO: Update system prompt.
        PromptTemplate systemPromptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('¶').endDelimiterToken('¶').build())
                .resource(generationSystemPrompt)
                .build();

        PromptTemplate userMessageTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('¶').endDelimiterToken('¶').build())
                .resource(generationUserPrompt)
                .build();

        FlashcardSet flashcardSet = flashcardSetService.findFlashcardSetById(options.getSetId());
        String flashcardSetJsonString = String.format("Title: %s\nDescription: %s\nFlashcards:\n%s",
                flashcardSet.getTitle(),
                flashcardSet.getDescription(),
                flashcardSet.getFlashcards().stream()
                        .map(f -> String.format("  Position: %d, Term: %s, Definition: %s",
                                f.getPosition(),
                                f.getTerm(),
                                f.getDefinition()))
                        .collect(Collectors.joining("\n")));

        String vectorQueryString = "Educational content for quiz generation related to the following set and it's details:\n" + flashcardSetJsonString;
        System.out.println(vectorQueryString);
        DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.75)
                .topK(5)
                .filterExpression(new FilterExpressionBuilder()
                        .eq("userId", authenticationService
                                .getAuthenticatedUser()
                                .getId()
                                .toString())
                        .build())
                .build();

        List<Document> contextDocuments = retriever.retrieve(new Query(vectorQueryString));
        String documentsContent = contextDocuments.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n---\n"));
        String questionTypesString = options.getQuestionTypes().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        String renderedSystemPrompt = systemPromptTemplate.render(Map.of(
                "k", options.getKQuestions(),
                "types", questionTypesString
        ));
        String renderedUserMessage = userMessageTemplate.render(Map.of(
                "documents", documentsContent
        ));

        ChatClient chatClient = chatClientBuilder.build();
        GeneratedQuiz generatedQuiz = chatClient
                .prompt()
                .system(renderedSystemPrompt)
                .user(renderedUserMessage)
                .options(OpenAiChatOptions.builder()
                        .model("gpt-4.1")
                        .temperature(0.3)
                        .responseFormat(ResponseFormat.builder()
                                .type(ResponseFormat.Type.JSON_SCHEMA)
                                .jsonSchema(GENERATED_QUIZ_SCHEMA)
                                .build())
                        .build())
                .call()
                .entity(GeneratedQuiz.class);
        return quizService.saveGeneratedQuiz(generatedQuiz, options.getSetId());
    }

    @Override
    public QuizAttempt gradeQuizFromInput(QuizAttemptInput quizInput) {
        System.out.println("First here");
        System.out.println(quizInput.toString());
        System.out.println("end first here");
        PromptTemplate systemPromptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('¶').endDelimiterToken('¶').build())
                .resource(gradeSystemPrompt)
                .build();
        PromptTemplate userPromptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('¶').endDelimiterToken('¶').build())
                .resource(gradeUserPrompt)
                .build();

        Quiz quiz = quizRepository.findById(quizInput.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found."));

        String quizString = quiz.toString();
        String flashcardSetString = quiz.getFlashcardSet().toString();

        String vectorQueryString = "Educational and insightful content for quiz grading related to the following quiz and it's details: "
                + quizString + "\n\n"
                + "The following set was used to generate the quiz:\n"
                + flashcardSetString;
        DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.75)
                .topK(12)
                .filterExpression(new FilterExpressionBuilder()
                        .eq("userId", authenticationService
                                .getAuthenticatedUser()
                                .getId()
                                .toString())
                        .build())
                .build();
        List<Document> contextDocuments = retriever.retrieve(new Query(vectorQueryString));
        String documentsContent = contextDocuments.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n---\n"));

        String renderedSystemPrompt = systemPromptTemplate.render(Map.of(
                "quiz", quizString,
                "flashcards", flashcardSetString
        ));
        String renderedUserMessage = userPromptTemplate.render(Map.of(
                "response", quizInput.toString(),
                "documents", documentsContent
        ));

        System.out.println("Quiz Input");
        System.out.println(quizInput.toString());
        System.out.println("End Quiz Input");

        ChatClient chatClient = chatClientBuilder.build();
        GradedQuiz gradedQuiz = chatClient
                .prompt()
                .system(renderedSystemPrompt)
                .user(renderedUserMessage)
                .options(OpenAiChatOptions.builder()
                        .model("gpt-4.1")
                        .temperature(0.25)
                        .responseFormat(ResponseFormat.builder()
                                .type(ResponseFormat.Type.JSON_SCHEMA)
                                .jsonSchema(GRADED_QUIZ_SCHEMA)
                                .build())
                        .build())
                .call()
                .entity(GradedQuiz.class);

        return quizAttemptService.createQuizAttemptFromGradedQuiz(gradedQuiz, quiz.getId());
    }

}

package com.github.monetadev.backend.service.ai.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.monetadev.backend.graphql.type.ai.quiz.generate.GeneratedQuiz;
import com.github.monetadev.backend.graphql.type.ai.quiz.grade.GradedQuiz;
import com.github.monetadev.backend.graphql.type.input.quiz.OptionInput;
import com.github.monetadev.backend.graphql.type.input.quiz.QuestionInput;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizGenOptions;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizInput;
import com.github.monetadev.backend.model.FlashcardSet;
import com.github.monetadev.backend.service.ai.QuizGenerationService;
import com.github.monetadev.backend.service.base.FlashcardSetService;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizGenerationServiceImpl implements QuizGenerationService {
    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;

    private final static String GENERATED_QUIZ_SCHEMA = """ 
            {
              "type": "object",
              "properties": {
                "title": {
                  "type": "string",
                  "description": "The title of the generated quiz."
                },
                "description": {
                  "type": "string",
                  "description": "A description of the generated quiz."
                },
                "questions": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "content": {
                        "type": "string",
                        "description": "The content or text of the question."
                      },
                      "position": {
                        "type": "integer",
                        "description": "The position of the question in the quiz."
                      },
                      "type": {
                        "type": "string",
                        "enum": [
                          "MULTIPLE_CHOICE_SINGLE_ANSWER",
                          "MULTIPLE_CHOICE_MULTIPLE_ANSWER",
                          "TRUE_FALSE",
                          "SHORT_ANSWER"
                        ],
                        "description": "The type of the question."
                      },
                      "options": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "content": {
                              "type": "string",
                              "description": "The content or text of the option."
                            },
                            "position": {
                              "type": "integer",
                              "description": "The position of the option."
                            }
                          },
                          "required": [
                            "content",
                            "position"
                          ],
                          "additionalProperties": false
                        },
                        "description": "A list of options for the question, if applicable."
                      }
                    },
                    "required": [
                      "content",
                      "position",
                      "type",
                      "options"
                    ],
                    "additionalProperties": false
                  },
                  "description": "A list of generated questions for the quiz."
                }
              },
              "required": [
                "title",
                "description",
                "questions"
              ],
              "additionalProperties": false
            }
            """;
    private final static String GRADED_QUIZ_SCHEMA = """
            {
              "type": "object",
              "properties": {
                "title": {
                  "type": "string",
                  "description": "The title of the graded quiz."
                },
                "description": {
                  "type": "string",
                  "description": "A description of the graded quiz."
                },
                "questions": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "content": {
                        "type": "string",
                        "description": "The content or text of the question."
                      },
                      "position": {
                        "type": "integer",
                        "description": "The position of the question in the quiz."
                      },
                      "userResponse": {
                        "type": "string",
                        "description": "The response provided by the user."
                      },
                      "isCorrectAnswer": {
                        "type": "boolean",
                        "description": "Indicates if the user's response was correct."
                      },
                      "feedback": {
                        "type": "string",
                        "description": "Feedback provided for the user's answer."
                      },
                      "options": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "content": {
                              "type": "string",
                              "description": "The content or text of the option."
                            },
                            "position": {
                              "type": "integer",
                              "description": "The position of the option."
                            },
                            "isCorrect": {
                              "type": "boolean",
                              "description": "Indicates if this option is a correct answer."
                            }
                          },
                          "required": [
                            "content",
                            "position",
                            "isCorrect"
                          ],
                          "additionalProperties": false
                        },
                        "description": "A list of options for the question, if applicable."
                      }
                    },
                    "required": [
                      "content",
                      "position",
                      "userResponse",
                      "isCorrectAnswer",
                      "feedback",
                      "options"
                    ],
                    "additionalProperties": false
                  },
                  "description": "A list of graded questions in the quiz."
                }
              },
              "required": [
                "title",
                "description",
                "questions"
              ],
              "additionalProperties": false
            }
            """;

    private final AuthenticationService authenticationService;
    private final Resource generationSystemPrompt;
    private final Resource generationUserPrompt;
    private final FlashcardSetService flashcardSetService;

    public QuizGenerationServiceImpl(ChatClient.Builder chatClientBuilder,
                                     VectorStore vectorStore,
                                     AuthenticationService authenticationService,
                                     @Value("classpath:ai/quiz/gen/system.st") Resource generationSystemPrompt,
                                     @Value("classpath:ai/quiz/gen/user.st") Resource generationUserPrompt, FlashcardSetService flashcardSetService) {
        this.chatClientBuilder = chatClientBuilder;
        this.vectorStore = vectorStore;
        this.authenticationService = authenticationService;
        this.generationSystemPrompt = generationSystemPrompt;
        this.generationUserPrompt = generationUserPrompt;
        this.flashcardSetService = flashcardSetService;
    }

    public GeneratedQuiz generateQuiz(QuizGenOptions options) {
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
        return chatClient
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
    }

    @Override
    public GradedQuiz gradeQuizFromInput(QuizInput quizInput) {
        // TODO: Implement Quiz grading.
        return null;
    }

    private String formatQuizInputQuestions(List<QuestionInput> questions) {
        if (questions == null || questions.isEmpty()) {
            return "No questions submitted.";
        }
        StringBuilder sb = new StringBuilder();
        for (QuestionInput q : questions) {
            sb.append("Question Position: ").append(q.getPosition()).append("\n");
            sb.append("Content: ").append(q.getContent()).append("\n");
            sb.append("Type: ").append(q.getType()).append("\n");
            sb.append("User Response: ").append(q.getUserResponse()).append("\n");
            if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                sb.append("Options:\n");
                for (OptionInput opt : q.getOptions()) {
                    sb.append("  - Position: ").append(opt.getPosition())
                            .append(", Content: ").append(opt.getContent()).append("\n");
                }
            }
            sb.append("---\n");
        }
        return sb.toString();
    }
}

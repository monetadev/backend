package com.github.monetadev.backend.service.base.impl;

import com.github.monetadev.backend.graphql.type.ai.quiz.generate.GeneratedOption;
import com.github.monetadev.backend.graphql.type.ai.quiz.generate.GeneratedQuestion;
import com.github.monetadev.backend.graphql.type.ai.quiz.generate.GeneratedQuiz;
import com.github.monetadev.backend.graphql.type.input.quiz.OptionInput;
import com.github.monetadev.backend.graphql.type.input.quiz.QuestionInput;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedQuiz;
import com.github.monetadev.backend.model.*;
import com.github.monetadev.backend.repository.FlashcardSetRepository;
import com.github.monetadev.backend.repository.QuizRepository;
import com.github.monetadev.backend.service.base.QuizService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final AuthenticationService authenticationService;
    private final FlashcardSetRepository flashcardSetRepository;

    public QuizServiceImpl(QuizRepository quizRepository,
                           AuthenticationService authenticationService,
                           FlashcardSetRepository flashcardSetRepository) {
        this.quizRepository = quizRepository;
        this.authenticationService = authenticationService;
        this.flashcardSetRepository = flashcardSetRepository;
    }

    @Override
    public Quiz findQuizById(UUID id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found with ID: " + id));
    }

    @Override
    @Transactional
    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public Quiz saveQuizFromInput(QuizInput quizInput) {
        User currentUser = authenticationService.getAuthenticatedUser();
        FlashcardSet flashcardSet = flashcardSetRepository.findById(quizInput.getSetId())
                .orElseThrow(() -> new NoSuchElementException("FlashcardSet not found with ID: " + quizInput.getSetId()));

        Quiz quiz = new Quiz();
        quiz.setTitle(quizInput.getTitle());
        quiz.setDescription(quizInput.getDescription());
        quiz.setAuthor(currentUser);
        quiz.setFlashcardSet(flashcardSet);

        List<Question> questions = new LinkedList<>();
        for (QuestionInput questionInput : quizInput.getQuestions()) {
            Question question = convertQuestionInputToQuestion(questionInput, quiz);
            questions.add(question);
        }
        quiz.setQuestions(questions);
        return quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public Quiz saveGeneratedQuiz(GeneratedQuiz generatedQuiz, UUID flashcardSetId) {
        User currentUser = authenticationService.getAuthenticatedUser();
        FlashcardSet flashcardSet = flashcardSetRepository.findById(flashcardSetId)
                .orElseThrow(() -> new NoSuchElementException("FlashcardSet not found with ID: " + flashcardSetId));

        Quiz quiz = new Quiz();
        quiz.setTitle(generatedQuiz.getTitle());
        quiz.setDescription(generatedQuiz.getDescription());
        quiz.setAuthor(currentUser);
        quiz.setFlashcardSet(flashcardSet);
        quiz.setQuestions(new ArrayList<>());

        for (GeneratedQuestion generatedQuestion : generatedQuiz.getQuestions()) {
            System.out.println("On question " + generatedQuestion.getPosition());
            Question question = mapQuestionTypes(generatedQuestion, quiz);
            quiz.getQuestions().add(question);
        }

        return quizRepository.save(quiz);
    }

    @NotNull
    private static Question mapQuestionTypes(GeneratedQuestion generatedQuestion, Quiz quiz) {
        Question question = new Question();
        question.setContent(generatedQuestion.getContent());
        question.setPosition(generatedQuestion.getPosition());
        question.setQuestionType(generatedQuestion.getQuestionType());
        question.setQuiz(quiz);

        if (generatedQuestion.getOptions() != null && !generatedQuestion.getOptions().isEmpty()) {
            List<Option> options = new ArrayList<>();
            for (GeneratedOption generatedOption : generatedQuestion.getOptions()) {
                Option option = new Option();
                option.setContent(generatedOption.getContent());
                option.setPosition(generatedOption.getPosition());
                option.setIsCorrect(generatedOption.getIsCorrect());
                option.setQuestion(question);
                options.add(option);
            }
            question.setOptions(options);
        }
        return question;
    }

    private Question convertQuestionInputToQuestion(QuestionInput questionInput, Quiz quiz) {
        Question question = new Question();
        question.setContent(questionInput.getContent());
        question.setPosition(questionInput.getPosition());
        question.setQuestionType(questionInput.getQuestionType());
        question.setQuiz(quiz);

        if (questionInput.getOptions() != null) {
            question.setOptions(questionInput.getOptions().stream()
                    .map(optionInput -> convertOptionInputToOption(optionInput, question))
                    .collect(Collectors.toList()));
        }

        return question;
    }

    private Option convertOptionInputToOption(OptionInput optionInput, Question question) {
        Option option = new Option();
        option.setContent(optionInput.getContent());
        option.setPosition(optionInput.getPosition());
        option.setIsCorrect(optionInput.getIsCorrect());
        option.setQuestion(question);
        return option;
    }


    @Override
    @Transactional
    public String deleteQuiz(UUID id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found with ID: " + id));
        String title = quiz.getTitle();
        quizRepository.delete(quiz);
        return title;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedQuiz getCurrentAuthenticatedUserQuizzes(int page, int size) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Page<Quiz> quizzes = quizRepository.findByAuthorId(authenticatedUser.getId(), PageRequest.of(page, size));

        return PaginatedQuiz.of()
                .items(quizzes.getContent())
                .totalPages(quizzes.getTotalPages())
                .totalElements(quizzes.getTotalElements())
                .currentPage(quizzes.getNumber())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedQuiz getFlashcardSetQuizzes(UUID setId, int page, int size) {
        Page<Quiz> quizzes = quizRepository.findByFlashcardSetId(setId, PageRequest.of(page, size));

        return PaginatedQuiz.of()
                .items(quizzes.getContent())
                .totalPages(quizzes.getTotalPages())
                .totalElements(quizzes.getTotalElements())
                .currentPage(quizzes.getNumber())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calculateFlashcardSetAverageQuizScore(UUID setId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calculateUserTotalAverageQuizScore(UUID userId) {
        return null;
    }
}

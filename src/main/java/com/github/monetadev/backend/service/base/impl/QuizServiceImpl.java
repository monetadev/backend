package com.github.monetadev.backend.service.base.impl;

import com.github.monetadev.backend.graphql.type.input.quiz.OptionInput;
import com.github.monetadev.backend.graphql.type.input.quiz.QuestionInput;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedQuiz;
import com.github.monetadev.backend.model.*;
import com.github.monetadev.backend.repository.FlashcardSetRepository;
import com.github.monetadev.backend.repository.QuizRepository;
import com.github.monetadev.backend.service.base.QuizService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
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
        quiz.setUser(currentUser);
        quiz.setFlashcardSet(flashcardSet);

        float correctCount = quizInput.getQuestions()
                .stream()
                .filter(QuestionInput::isCorrectUserResponse)
                .count();
        float grade = quizInput.getQuestions().isEmpty() ? 0 : correctCount / quizInput.getQuestions().size();
        quiz.setGrade(grade);

        Set<Question> questions = new HashSet<>();
        for (QuestionInput questionInput : quizInput.getQuestions()) {
            Question question = convertQuestionInputToQuestion(questionInput, quiz);
            questions.add(question);
        }
        quiz.setQuestions(questions);
        return quizRepository.save(quiz);
    }

    private Question convertQuestionInputToQuestion(QuestionInput questionInput, Quiz quiz) {
        Question question = new Question();
        question.setContent(questionInput.getContent());
        question.setPosition(questionInput.getPosition());
        question.setQuestionType(questionInput.getType());
        question.setUserResponse(questionInput.getUserResponse());
        question.setIsCorrectUserResponse(questionInput.isCorrectUserResponse());
        question.setFeedback(questionInput.getFeedback());
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
        Page<Quiz> quizzes = quizRepository.findByUserId(authenticatedUser.getId(), PageRequest.of(page, size));

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
        Double averageScore = quizRepository.findAverageScoreByFlashcardSetId(setId);
        return (averageScore == null) ? 0 : averageScore.intValue();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calculateUserAverageQuizScore(UUID userId) {
        Double averageScore = quizRepository.findAverageScoreByUserId(userId);
        return (averageScore == null) ? 0 : averageScore.intValue();
    }
}

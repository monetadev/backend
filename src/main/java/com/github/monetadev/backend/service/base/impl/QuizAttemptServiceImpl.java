package com.github.monetadev.backend.service.base.impl;

import com.github.monetadev.backend.graphql.type.ai.quiz.grade.GradedQuestion;
import com.github.monetadev.backend.graphql.type.ai.quiz.grade.GradedQuiz;
import com.github.monetadev.backend.graphql.type.input.quiz.QuestionInput;
import com.github.monetadev.backend.graphql.type.input.quiz.QuizAttemptInput;
import com.github.monetadev.backend.graphql.type.pagination.PaginatedQuizAttempt;
import com.github.monetadev.backend.model.*;
import com.github.monetadev.backend.repository.*;
import com.github.monetadev.backend.service.base.QuizAttemptService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizAttemptServiceImpl implements QuizAttemptService {
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final AuthenticationService authenticationService;

    public QuizAttemptServiceImpl(QuizAttemptRepository quizAttemptRepository,
                                  QuizRepository quizRepository,
                                  AuthenticationService authenticationService) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizRepository = quizRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    @Transactional
    public QuizAttempt createQuizAttemptFromQuizAttemptInput(QuizAttemptInput quizAttemptInput) {
        if (quizAttemptInput.getQuizId() == null) {
            throw new IllegalArgumentException("Quiz ID is required");
        }

        User currentUser = authenticationService.getAuthenticatedUser();

        Quiz quiz = quizRepository.findById(quizAttemptInput.getQuizId())
                .orElseThrow(() -> new NoSuchElementException("Quiz not found with ID: " + quizAttemptInput.getQuizId()));

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setQuiz(quiz);
        quizAttempt.setUser(currentUser);
        quizAttempt = quizAttemptRepository.save(quizAttempt);

        List<QuizAttemptUserQuestionResponse> responses = new ArrayList<>();

        // Create a map of question positions to Question entities for easy lookup
        Map<Integer, Question> questionsByPosition = quiz.getQuestions().stream()
                .collect(Collectors.toMap(Question::getPosition, q -> q));

        if (quizAttemptInput.getResponses() != null && !quizAttemptInput.getResponses().isEmpty()) {
            for (QuestionInput questionInput : quizAttemptInput.getResponses()) {
                // Find the question by position
                Question question = questionsByPosition.get(questionInput.getPosition());
                if (question == null) {
                    continue; // Skip if question not found
                }

                QuizAttemptUserQuestionResponse response = new QuizAttemptUserQuestionResponse();
                response.setQuestion(question);
                response.setResponse(questionInput.getContent());
                response.setQuizAttempt(quizAttempt);

                // Determine if the response is correct (simplified logic - would need more complex validation)
                boolean isCorrect = isResponseCorrect(question, questionInput.getContent());
                response.setIsCorrect(isCorrect);

                responses.add(response);
            }
        }

        quizAttempt.setResponses(responses);

        int score = calculateScoreFromResponses(responses, quiz.getQuestions().size());
        quizAttempt.setScore(score);

        return quizAttemptRepository.save(quizAttempt);
    }

    @Transactional
    public QuizAttempt createQuizAttemptFromGradedQuiz(GradedQuiz gradedQuiz, UUID quizId) {
        User user = authenticationService.getAuthenticatedUser();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found with ID: " + quizId));
        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setQuiz(quiz);
        quizAttempt.setUser(user);
        quizAttempt = quizAttemptRepository.save(quizAttempt);

        List<QuizAttemptUserQuestionResponse> responses = new ArrayList<>();

        Map<Integer, Question> questionByPosition = quiz.getQuestions().stream()
                .collect(Collectors.toMap(Question::getPosition, q -> q));

        for (GradedQuestion gradedQuestion : gradedQuiz.getQuestions()) {
            Question question = questionByPosition.get(gradedQuestion.getPosition());
            if (question == null) {
                continue;
            }

            QuizAttemptUserQuestionResponse response = new QuizAttemptUserQuestionResponse();
            response.setQuestion(question);
            response.setResponse(gradedQuestion.getUserResponse());
            response.setIsCorrect(gradedQuestion.isCorrectAnswer());
            response.setFeedback(gradedQuestion.getFeedback());
            response.setQuizAttempt(quizAttempt);

            responses.add(response);
        }

        quizAttempt.setResponses(responses);

        int totalQuestions = gradedQuiz.getQuestions().size();
        long correctAnswers = gradedQuiz.getQuestions().stream()
                .filter(GradedQuestion::isCorrectAnswer)
                .count();

        int score = totalQuestions > 0 ? (int) Math.round((double) correctAnswers / totalQuestions * 100) : 0;
        quizAttempt.setScore(score);

        return quizAttemptRepository.save(quizAttempt);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizAttempt getQuizAttempt(UUID attemptId) {
        return quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new NoSuchElementException("Quiz attempt not found with ID: " + attemptId));
    }

    @Override
    @Transactional
    public String deleteQuizAttempt(UUID attemptId) {
        QuizAttempt quizAttempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new NoSuchElementException("Quiz attempt not found with ID: " + attemptId));
        String title = quizAttempt.getQuiz().getTitle();
        quizAttemptRepository.delete(quizAttempt);
        return title;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedQuizAttempt getUserQuizAttempts(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizAttempt> attemptsPage = quizAttemptRepository.findByUserId(userId, pageable);

        return createPaginatedResponse(attemptsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedQuizAttempt getQuizAttemptsByQuizId(UUID quizId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizAttempt> attemptsPage = quizAttemptRepository.findByQuizId(quizId, pageable);

        return createPaginatedResponse(attemptsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedQuizAttempt getUserQuizAttemptsByQuizId(UUID userId, UUID quizId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizAttempt> attemptsPage = quizAttemptRepository.findByUserIdAndQuizId(userId, quizId, pageable);

        return createPaginatedResponse(attemptsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageScoreForUser(UUID userId) {
        Double averageScore = quizAttemptRepository.getAverageScoreByUserId(userId);
        return averageScore != null ? averageScore : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageScoreForQuiz(UUID quizId) {
        Double averageScore = quizAttemptRepository.getAverageScoreByQuizId(quizId);
        return averageScore != null ? averageScore : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageScoreForUserAndQuiz(UUID userId, UUID quizId) {
        Double averageScore = quizAttemptRepository.getAverageScoreByUserIdAndQuizId(userId, quizId);
        return averageScore != null ? averageScore : 0.0;
    }

    private int calculateScoreFromResponses(List<QuizAttemptUserQuestionResponse> responses, int totalQuestions) {
        if (responses == null || responses.isEmpty() || totalQuestions == 0) {
            return 0;
        }

        long correctAnswers = responses.stream()
                .filter(QuizAttemptUserQuestionResponse::getIsCorrect)
                .count();

        return (int) Math.round((double) correctAnswers / totalQuestions * 100);
    }

    private PaginatedQuizAttempt createPaginatedResponse(Page<QuizAttempt> page) {
        return PaginatedQuizAttempt.of()
                .items(page.getContent())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .currentPage(page.getNumber())
                .build();
    }

    /**
     * Determines if a user's response is correct for a given question.
     * This is a simplified implementation that would need to be expanded based on question type.
     *
     * @param question The question entity
     * @param userResponse The user's response content
     * @return True if the response is correct, false otherwise
     */
    private boolean isResponseCorrect(Question question, String userResponse) {
        if (userResponse == null) {
            return false;
        }

        switch (question.getQuestionType()) {
            case MULTIPLE_CHOICE_SINGLE_ANSWER:
            case MULTIPLE_CHOICE_MULTIPLE_ANSWER:
                // For multiple choice, validate against the correct options
                return question.getOptions().stream()
                        .filter(Option::getIsCorrect)
                        .anyMatch(option -> userResponse.contains(option.getContent()));

            case TRUE_FALSE:
                // For true/false, check if response matches correct option
                return question.getOptions().stream()
                        .filter(Option::getIsCorrect)
                        .anyMatch(option -> userResponse.equalsIgnoreCase(option.getContent()));

            case SHORT_ANSWER:
                // For short answer, could implement more complex validation
                // This is a simplified version
                String correctAnswer = question.getOptions().stream()
                        .filter(Option::getIsCorrect)
                        .findFirst()
                        .map(Option::getContent)
                        .orElse("");
                return userResponse.trim().equalsIgnoreCase(correctAnswer.trim());

            default:
                return false;
        }
    }
}
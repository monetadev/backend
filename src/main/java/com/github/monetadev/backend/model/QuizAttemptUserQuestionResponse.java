package com.github.monetadev.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"id", "quizAttempt"})
@Entity
public class QuizAttemptUserQuestionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "quiz_attempt_user_question_response_id")
    private UUID id;

    @Column(name = "response", nullable = false, length = 10000)
    private String response;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "feedback", length = 10000)
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}

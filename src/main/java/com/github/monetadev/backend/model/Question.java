package com.github.monetadev.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"id", "responses", "quiz"})
@Entity
public class Question {
    @Id
    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content", nullable = false, length = 10000)
    private String content;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @OneToMany(
            mappedBy = "question",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Option> options;

    @OneToMany(
            mappedBy = "question",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<QuizAttemptUserQuestionResponse> responses;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    public enum QuestionType {
        MULTIPLE_CHOICE_SINGLE_ANSWER,
        MULTIPLE_CHOICE_MULTIPLE_ANSWER,
        TRUE_FALSE,
        SHORT_ANSWER
    }
}

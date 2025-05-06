package com.github.monetadev.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class Question {
    @Id
    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "user_response", nullable = false)
    private String userResponse;

    @Column(name = "is_correct_user_response", nullable = false)
    private Boolean isCorrectUserResponse;

    @Column(name = "feedback", nullable = false)
    private String feedback;

    @OneToMany(
            mappedBy = "question",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Option> options;

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

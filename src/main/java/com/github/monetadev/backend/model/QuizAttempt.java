package com.github.monetadev.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class QuizAttempt {
    @Id
    @Column(name = "quiz_attempt_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "attempt_date", updatable = false)
    private OffsetDateTime attemptDate;

    @OneToMany(
            mappedBy = "quizAttempt",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuizAttemptUserQuestionResponse> responses;

    @Column(name = "score")
    private Integer score;
}

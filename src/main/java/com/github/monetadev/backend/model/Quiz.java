package com.github.monetadev.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class Quiz {
    @Id
    @Column(name = "quiz_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "grade", nullable = false)
    private Float grade;

    @ManyToOne
    @JoinColumn(name = "quiz_user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "flashcard_set_id", nullable = false, updatable = false)
    private FlashcardSet flashcardSet;

    @CreationTimestamp
    @Column(name = "creation_date")
    private OffsetDateTime creationDate;

    @OneToMany(
            mappedBy = "quiz",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Question> questions;
}

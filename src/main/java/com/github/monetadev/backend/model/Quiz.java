package com.github.monetadev.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"id", "author", "flashcardSet"})
@Entity
public class Quiz {
    @Id
    @Column(name = "quiz_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 10000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private User author;

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
    private List<Question> questions;
}

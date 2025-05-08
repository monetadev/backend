package com.github.monetadev.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"id", "flashcardSet"})
@Entity
public class Flashcard {
    @Id
    @Column(name = "flashcard_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "term", nullable = false, length = 10000)
    private String term;

    @Column(name = "definition", nullable = false, length = 10000)
    private String definition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_set_id", nullable = false, updatable = false)
    private FlashcardSet flashcardSet;
}

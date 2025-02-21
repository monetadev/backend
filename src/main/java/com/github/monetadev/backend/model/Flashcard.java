package com.github.monetadev.backend.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Flashcard {
    @Id
    @Column(name = "flashcard_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "term", nullable = false)
    private String term;

    @Column(name = "definition", nullable = false)
    private String definition;

    @ManyToOne
    @JoinColumn(name = "flashcard_set_id", nullable = false, updatable = false)
    private FlashcardSet flashcardSet;
}

package com.github.monetadev.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class FlashcardSet {
    @Id
    @Column(name = "flashcard_set_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "public", nullable = false)
    private Boolean isPublic;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "last_updated")
    private Date lastUpdated;

    @OneToMany
    private List<Flashcard> flashcards;

    @ManyToOne
    @JoinColumn(name = "author_user_id", nullable = false, updatable = false)
    private User author;

}
package com.github.monetadev.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
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

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @CreationTimestamp
    @Column(name = "creation_date")
    private OffsetDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

    @OneToMany(
            mappedBy = "flashcardSet",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Flashcard> flashcards;

    @ManyToOne
    @JoinColumn(name = "author_user_id", nullable = false, updatable = false)
    private User author;
}
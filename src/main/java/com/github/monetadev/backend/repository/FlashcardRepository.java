package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, UUID> {
    List<Flashcard> findAllByFlashcardSetId(UUID flashcardSetId);
}

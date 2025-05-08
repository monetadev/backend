package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    Page<Quiz> findByAuthorId(UUID userId, Pageable pageable);
    Page<Quiz> findByFlashcardSetId(UUID flashcardSetId, Pageable pageable);
}
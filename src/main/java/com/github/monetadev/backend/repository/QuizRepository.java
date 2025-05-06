package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    Page<Quiz> findByUserId(UUID userId, Pageable pageable);

    Page<Quiz> findByFlashcardSetId(UUID flashcardSetId, Pageable pageable);

    @Query("SELECT AVG(q.grade) FROM Quiz q WHERE q.flashcardSet.id = :setId")
    Double findAverageScoreByFlashcardSetId(@Param("setId") UUID setId);

    @Query("SELECT AVG(q.grade) FROM Quiz q WHERE q.user.id = :userId")
    Double findAverageScoreByUserId(@Param("userId") UUID userId);
}
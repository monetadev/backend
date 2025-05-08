package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.QuizAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    Page<QuizAttempt> findByUserId(UUID userId, Pageable pageable);
    Page<QuizAttempt> findByQuizId(UUID quizId, Pageable pageable);
    Page<QuizAttempt> findByUserIdAndQuizId(UUID userId, UUID quizId, Pageable pageable);

    @Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.user.id = :userId")
    Double getAverageScoreByUserId(@Param("userId") UUID userId);

    @Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId")
    Double getAverageScoreByQuizId(@Param("quizId") UUID quizId);

    @Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.quiz.id = :quizId")
    Double getAverageScoreByUserIdAndQuizId(@Param("userId") UUID userId, @Param("quizId") UUID quizId);
}
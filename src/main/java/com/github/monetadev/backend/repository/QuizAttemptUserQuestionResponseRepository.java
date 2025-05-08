package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.QuizAttemptUserQuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuizAttemptUserQuestionResponseRepository extends JpaRepository<QuizAttemptUserQuestionResponse, UUID> {
}

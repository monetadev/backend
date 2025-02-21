package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.FlashcardSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, UUID> {
    List<FlashcardSet> findAllByAuthorId(UUID authorId);

    List<FlashcardSet> findByIsPublicIsTrue();
}

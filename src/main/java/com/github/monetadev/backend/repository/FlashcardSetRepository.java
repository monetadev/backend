package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.FlashcardSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, UUID> {
    Page<FlashcardSet> findAllByAuthorId(UUID authorId, Pageable pageable);

    Page<FlashcardSet> findAllByIsPublic(Boolean isPublic, Pageable pageable);
}

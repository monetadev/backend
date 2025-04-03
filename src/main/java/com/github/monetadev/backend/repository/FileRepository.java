package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.File;
import com.github.monetadev.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
    List<File> getFilesByUserId(UUID id);
    Optional<File> findByUserAndFilename(User user, String filename);
    Optional<File> findFileByFilePathContainsAndUserId(String filepath, UUID userId);
}

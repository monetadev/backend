package com.github.monetadev.backend.service.ai;

import com.github.monetadev.backend.model.File;

import java.util.UUID;

public interface DocumentEmbedService {
    String embedFile(File file);
    void deleteEmbeddingById(UUID id);
}

package com.github.monetadev.backend.service.file;

import com.github.monetadev.backend.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileTypeService {
    /**
     * Get the directory name for this file questionType
     */
    String getDirectoryName();

    /**
     * Get allowed MIME types for this file questionType
     */
    List<String> getAllowedMimeTypes();

    /**
     * Validate a file of this questionType
     */
    void validateFile(MultipartFile file);

    /**
     * Process and save a file of this questionType
     */
    File processAndSaveFile(MultipartFile file) throws IOException;
}

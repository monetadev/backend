package com.github.monetadev.backend.service.file;

import com.github.monetadev.backend.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileTypeService {
    /**
     * Get the directory name for this file type
     */
    String getDirectoryName();

    /**
     * Get allowed MIME types for this file type
     */
    List<String> getAllowedMimeTypes();

    /**
     * Get maximum file size in bytes
     */
    long getMaxFileSize();

    /**
     * Validate a file of this type
     */
    void validateFile(MultipartFile file);

    /**
     * Process and save a file of this type
     */
    File processAndSaveFile(MultipartFile file) throws IOException;
}

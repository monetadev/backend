package com.github.monetadev.backend.service.file;

import com.github.monetadev.backend.model.File;
import com.github.monetadev.backend.model.User;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;

public interface PersistenceService {
    /**
     * Saves a file to the configured data directory and creates file metadata
     *
     * @param file The file to save
     * @param relativePath The path relative to the data directory
     * @param user The owner of the file
     * @param customFilename Optional custom filename (if null, generates unique filename)
     * @param preserveExtension Whether to preserve the file extension
     * @return The created File entity with metadata
     */
    File saveFileWithMetadata(MultipartFile file, String relativePath, User user,
                              String customFilename, boolean preserveExtension) throws IOException;

    /**
     * Generates a safe filename for the given original filename
     */
    String generateSafeFilename(String originalFilename, boolean preserveExtension);

    /**
     * Validates and normalizes a relative path to prevent directory traversal
     */
    String validateAndNormalizePath(String relativePath);

    /**
     * Gets the absolute path from the data directory and relative path
     */
    Path resolveAbsolutePath(String relativePath);

    /**
     * Calculates the hash of the supplied file.
     */
    String calculateHash(MultipartFile file);

    // Existing methods...

    /**
     * Deletes a file from both the filesystem and database
     *
     * @param file The file entity to delete
     * @return true if successfully deleted, false otherwise
     * @throws IOException If an I/O error occurs during file deletion
     */
    boolean deleteFile(File file) throws IOException;

    /**
     * Deletes a file from the filesystem only
     *
     * @param filePath Path to the file to delete
     * @return true if successfully deleted, false otherwise
     * @throws IOException If an I/O error occurs during file deletion
     */
    boolean deleteFileFromFilesystem(String filePath) throws IOException;
}

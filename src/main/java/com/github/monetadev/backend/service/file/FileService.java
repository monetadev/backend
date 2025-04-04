package com.github.monetadev.backend.service.file;

import com.github.monetadev.backend.model.File;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

public interface FileService {
    /**
     * Saves a new file to the system.
     *
     * @param file The file entity to be saved
     * @return The saved file with populated system-generated fields
     */
    File saveFile(File file);

    /**
     * Updates an existing file in the system.
     *
     * @param file The file entity containing updated information
     * @return The updated file entity
     */
    File updateFile(File file);

    /**
     * Deletes a file from the system.
     *
     * @param file The file entity to be deleted
     * @return The UUID of the deleted file
     */
    UUID deleteFile(File file);

    /**
     * Deletes a file from the system.
     *
     * @param id the UUID of the file entity to be deleted
     * @return the UUID of the deleted file
     */
    UUID deleteFile(UUID id);

    /**
     * Retrieves a specific file by its unique identifier.
     *
     * @param id The unique identifier of the file
     * @return The file entity if found
     */
    File getFileById(UUID id);

    /**
     * Retrieves all files associated with a specific user.
     *
     * @param id The unique identifier of the user
     * @return A list of files belonging to the specified user
     */
    List<File> getFilesByUserId(UUID id);

    /**
     * Retrieves the file from the filesystem as a {@link Resource}.
     * @param file the file entity to be searched on.
     * @return The file as a Resource.
     */
    Resource getResourceByFile(File file);
}


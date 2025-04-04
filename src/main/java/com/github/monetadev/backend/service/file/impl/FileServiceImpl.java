package com.github.monetadev.backend.service.file.impl;

import com.github.monetadev.backend.config.prop.StorageProperties;
import com.github.monetadev.backend.exception.FileNotFoundException;
import com.github.monetadev.backend.exception.StorageException;
import com.github.monetadev.backend.exception.StorageFileNotFoundException;
import com.github.monetadev.backend.model.File;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.FileRepository;
import com.github.monetadev.backend.service.file.FileService;
import com.github.monetadev.backend.service.file.PersistenceService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final AuthenticationService authenticationService;
    private final StorageProperties storageProperties;
    private final PersistenceService persistenceService;

    public FileServiceImpl(FileRepository fileRepository,
                           AuthenticationService authenticationService,
                           StorageProperties storageProperties, PersistenceService persistenceService) {
        this.fileRepository = fileRepository;
        this.authenticationService = authenticationService;
        this.storageProperties = storageProperties;
        this.persistenceService = persistenceService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File saveFile(File file) {
        User currentUser = authenticationService.getAuthenticatedUser();
        file.setUser(currentUser);
        return fileRepository.save(file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File updateFile(File file) {
        return fileRepository.save(file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UUID deleteFile(File file) {
        UUID rv = file.getId();
        file.getUser().getFiles().remove(file);
        persistenceService.deleteFile(file);
        fileRepository.delete(file);
        return rv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UUID deleteFile(UUID id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id.toString()));
        file.getUser().getFiles().remove(file);
        persistenceService.deleteFile(file);
        fileRepository.delete(file);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFileById(UUID id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found with id: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<File> getFilesByUserId(UUID id) {
        return fileRepository.getFilesByUserId(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getResourceByFile(File file) {
        Path filePath = Paths.get(storageProperties.getDataDirectory(), file.getFilePath());
        if (!Files.exists(filePath)) {
            throw new StorageFileNotFoundException("File not found with path: " + filePath);
        }
        try {
            return new FileSystemResource(filePath.toFile());
        } catch (Exception ignored) {
            throw new StorageException("File was found, but could not be read: " + filePath);
        }
    }
}

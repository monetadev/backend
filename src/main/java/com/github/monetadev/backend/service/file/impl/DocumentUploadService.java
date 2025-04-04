package com.github.monetadev.backend.service.file.impl;

import com.github.monetadev.backend.config.prop.FileProperties;
import com.github.monetadev.backend.exception.DocumentUploadException;
import com.github.monetadev.backend.exception.InvalidFileUploadException;
import com.github.monetadev.backend.graphql.type.file.DocumentUploadResult;
import com.github.monetadev.backend.model.File;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.FileRepository;
import com.github.monetadev.backend.service.file.FileTypeService;
import com.github.monetadev.backend.service.file.PersistenceService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentUploadService implements FileTypeService {
    private final FileProperties fileProperties;
    private final AuthenticationService authenticationService;
    private final PersistenceService persistenceService;
    private final FileRepository fileRepository;

    @Autowired
    public DocumentUploadService(FileProperties fileProperties, AuthenticationService authenticationService, PersistenceService persistenceService, FileRepository fileRepository) {
        this.fileProperties = fileProperties;
        this.authenticationService = authenticationService;
        this.persistenceService = persistenceService;
        this.fileRepository = fileRepository;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDirectoryName() {
        return fileProperties.getDocumentDirName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllowedMimeTypes() {
        return List.of(fileProperties.getDocumentMimeType().split(","));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getMaxFileSize() {
        return fileProperties.getDocumentMaxSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileUploadException("Document file is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !getAllowedMimeTypes().contains(contentType)) {
            throw new InvalidFileUploadException(
                    "Invalid document type. Allowed types: " + String.join(", ", getAllowedMimeTypes())
            );
        }

        if (file.getSize() > getMaxFileSize()) {
            throw new InvalidFileUploadException(
                    "Document exceeds maximum size of " + (getMaxFileSize() / (1024 * 1024)) + "MB"
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File processAndSaveFile(MultipartFile file) {
        User user = authenticationService.getAuthenticatedUser();
        validateFile(file);

        try {
            return persistenceService.saveFileWithMetadata(
                    file,
                    getDirectoryName(),
                    user,
                    null,
                    true
            );
        } catch (IOException e) {
            throw new DocumentUploadException("Could not save document");
        }
    }

    /**
     * Upload a document and return structured result
     */
    @Transactional
    public DocumentUploadResult uploadDocument(MultipartFile file) {
        File fileMetadata = processAndSaveFile(file);

        return DocumentUploadResult.builder()
                .filename(fileMetadata.getFilename())
                .originalFilename(fileMetadata.getOriginalFilename())
                .size(fileMetadata.getFileSize())
                .contentType(fileMetadata.getContentType())
                .path(fileMetadata.getFilePath())
                .build();
    }

    @Transactional
    protected boolean deleteDocument(File document) {
        if (document == null) {
            return false;
        }

        return persistenceService.deleteFile(document);
    }

    @Transactional
    public boolean deleteUserDocument(UUID documentId) {
        if (documentId == null) {
            return false;
        }

        User user = authenticationService.getAuthenticatedUser();
        Optional<File> document = fileRepository.findByIdAndUser(documentId, user);

        return document.map(this::deleteDocument).orElse(false);
    }

    @Transactional
    public List<File> getUserDocuments() {
        User user = authenticationService.getAuthenticatedUser();
        return fileRepository.findAllByFilePathContainsAndUser(getDirectoryName(), user);
    }
}

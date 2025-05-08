package com.github.monetadev.backend.service.file.impl;

import com.github.monetadev.backend.config.prop.FileProperties;
import com.github.monetadev.backend.exception.DocumentDeleteException;
import com.github.monetadev.backend.exception.DocumentUploadException;
import com.github.monetadev.backend.exception.InvalidFileUploadException;
import com.github.monetadev.backend.graphql.type.file.DocumentUploadResult;
import com.github.monetadev.backend.model.File;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.FileRepository;
import com.github.monetadev.backend.service.ai.DocumentEmbedService;
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
    private final DocumentEmbedService documentEmbedService;

    @Autowired
    public DocumentUploadService(FileProperties fileProperties, AuthenticationService authenticationService, PersistenceService persistenceService, FileRepository fileRepository, DocumentEmbedService documentEmbedService) {
        this.fileProperties = fileProperties;
        this.authenticationService = authenticationService;
        this.persistenceService = persistenceService;
        this.fileRepository = fileRepository;
        this.documentEmbedService = documentEmbedService;
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
    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileUploadException("Document file is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !getAllowedMimeTypes().contains(contentType)) {
            throw new InvalidFileUploadException(
                    "Invalid document questionType. Allowed types: " + String.join(", ", getAllowedMimeTypes())
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
        documentEmbedService.embedFile(fileMetadata);

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
        documentEmbedService.deleteEmbeddingById(document.getId());

        return persistenceService.deleteFile(document);
    }

    @Transactional
    public UUID deleteUserDocument(UUID documentId) {
        User user = authenticationService.getAuthenticatedUser();
        File document = fileRepository.findByIdAndUser(documentId, user)
                .orElseThrow(() -> new DocumentDeleteException("Document not found with id: " + documentId));

        return deleteDocument(document) ? documentId : null;
    }

    @Transactional
    public List<File> getUserDocuments() {
        User user = authenticationService.getAuthenticatedUser();
        return fileRepository.findAllByFilePathContainsAndUser(getDirectoryName(), user);
    }
}

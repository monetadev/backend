package com.github.monetadev.backend.service.file.impl;

import com.github.monetadev.backend.config.prop.FileProperties;
import com.github.monetadev.backend.exception.InvalidFileUploadException;
import com.github.monetadev.backend.exception.ProfilePictureUploadException;
import com.github.monetadev.backend.graphql.type.file.ImageUploadResult;
import com.github.monetadev.backend.model.File;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.FileRepository;
import com.github.monetadev.backend.service.base.UserService;
import com.github.monetadev.backend.service.file.FileService;
import com.github.monetadev.backend.service.file.FileTypeService;
import com.github.monetadev.backend.service.file.PersistenceService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfilePictureService implements FileTypeService {
    private final PersistenceService persistenceService;
    private final AuthenticationService authenticationService;
    private final FileRepository fileRepository;
    private final UserService userService;
    private final FileProperties fileProperties;
    private final FileService fileService;

    public ProfilePictureService(PersistenceService persistenceService,
                                 AuthenticationService authenticationService,
                                 FileRepository fileRepository,
                                 UserService userService,
                                 FileProperties fileProperties, FileService fileService) {
        this.persistenceService = persistenceService;
        this.authenticationService = authenticationService;
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.fileProperties = fileProperties;
        this.fileService = fileService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDirectoryName() {
        return fileProperties.getProfilePictureDirName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllowedMimeTypes() {
        return Arrays.asList(fileProperties.getProfilePictureMimeType().split(","));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileUploadException("Image file is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !getAllowedMimeTypes().contains(contentType)) {
            throw new InvalidFileUploadException(
                    "Invalid image questionType. Allowed types: " + String.join(", ", getAllowedMimeTypes())
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public File processAndSaveFile(MultipartFile file) {
        User user = authenticationService.getAuthenticatedUser();
        Optional<File> candidate = fileRepository.findFileByFilePathContainsAndUserId(fileProperties.getProfilePictureDirName(), user.getId());
        if (candidate.isPresent()) {
            throw new ProfilePictureUploadException("User already has a profile picture!");
        }
        validateFile(file);

        try {
            // Easily accessible routing for frontend, omit extension.
            String userId = user.getId().toString();
            return persistenceService.saveFileWithMetadata(
                    file,
                    getDirectoryName(),
                    user,
                    userId,
                    false
            );
        } catch (IOException e) {
            throw new ProfilePictureUploadException("Could not save profile picture");
        }
    }

    /**
     * Upload a profile picture and return structured result
     */
    @Transactional
    public ImageUploadResult uploadProfileImage(MultipartFile file) {
        File fileMetadata = processAndSaveFile(file);

        return ImageUploadResult.builder()
                .filename(fileMetadata.getFilename())
                .originalFilename(fileMetadata.getOriginalFilename())
                .size(fileMetadata.getFileSize())
                .contentType(fileMetadata.getContentType())
                .path(fileMetadata.getFilePath())
                .build();
    }

    @Transactional
    protected boolean deleteUserProfilePicture(User user) {
        if (user == null) {
            return false;
        }
        Optional<File> existingProfilePicture = fileRepository.findByUserAndFilename(user, user.getId().toString());
        if (existingProfilePicture.isPresent()) {
            fileService.deleteFile(existingProfilePicture.get());
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteCurrentUserProfilePicture() {
        User user = authenticationService.getAuthenticatedUser();
        return deleteUserProfilePicture(user);
    }

    @Transactional
    public boolean deleteUserProfilePicture(UUID userId) {
        if (userId == null) {
            return false;
        }
        User user = userService.findUserById(userId);
        return deleteUserProfilePicture(user);
    }
}

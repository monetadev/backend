package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.graphql.type.file.DocumentUploadResult;
import com.github.monetadev.backend.graphql.type.file.ImageUploadResult;
import com.github.monetadev.backend.service.file.FileService;
import com.github.monetadev.backend.service.file.impl.DocumentUploadService;
import com.github.monetadev.backend.service.file.impl.ProfilePictureService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@DgsComponent
public class UploadController {

    private final ProfilePictureService profilePictureService;
    private final DocumentUploadService documentUploadService;
    private final FileService fileService;

    public UploadController(ProfilePictureService profilePictureService, DocumentUploadService documentUploadService, FileService fileService) {
        this.profilePictureService = profilePictureService;
        this.documentUploadService = documentUploadService;
        this.fileService = fileService;
    }

    @DgsMutation
    public ImageUploadResult uploadProfilePicture(DataFetchingEnvironment env) {
        MultipartFile file = env.getArgument("input");
        return profilePictureService.uploadProfileImage(file);
    }

    @DgsMutation
    public boolean deleteCurrentUserProfilePicture() {
        return profilePictureService.deleteCurrentUserProfilePicture();
    }

    @DgsMutation
    public boolean deleteUserProfilePicture(@InputArgument UUID id) {
        return profilePictureService.deleteUserProfilePicture(id);
    }

    @DgsMutation
    public DocumentUploadResult uploadDocument(DataFetchingEnvironment env) {
        MultipartFile file = env.getArgument("input");
        return documentUploadService.uploadDocument(file);
    }

    @DgsMutation
    public UUID deleteDocument(@InputArgument UUID id) {
        return documentUploadService.deleteUserDocument(id);
    }
}

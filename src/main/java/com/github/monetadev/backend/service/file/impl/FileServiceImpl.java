package com.github.monetadev.backend.service.file.impl;

import com.github.monetadev.backend.exception.FileNotFoundException;
import com.github.monetadev.backend.model.File;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.FileRepository;
import com.github.monetadev.backend.service.file.FileService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final AuthenticationService authenticationService;

    public FileServiceImpl(FileRepository fileRepository, AuthenticationService authenticationService) {
        this.fileRepository = fileRepository;
        this.authenticationService = authenticationService;
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
    public UUID deleteFile(File file) {
        UUID rv = file.getId();
        file.getUser().getFiles().remove(file);
        fileRepository.delete(file);
        return rv;
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
}

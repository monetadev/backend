package com.github.monetadev.backend.service.file.impl;

import com.github.monetadev.backend.config.prop.StorageProperties;
import com.github.monetadev.backend.exception.StorageException;
import com.github.monetadev.backend.model.File;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.FileRepository;
import com.github.monetadev.backend.service.file.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class PersistenceServiceImpl implements PersistenceService {
    private static final Pattern SAFE_PATH_PATTERN = Pattern.compile("^[a-zA-Z0-9_/\\-]+$");
    private static final String DEFAULT_PROJECT_FOLDER = ".monetadev";

    private final Path dataDirectoryPath;
    private final FileRepository fileRepository;
    private final StorageProperties storageProperties;

    @Autowired
    public PersistenceServiceImpl(FileRepository fileRepository, StorageProperties storageProperties) {
        this.storageProperties = storageProperties;

        String dataDir = storageProperties.getDataDirectory();
        if (dataDir == null || dataDir.isEmpty()) {
            dataDir = Paths.get(System.getProperty("user.home"), DEFAULT_PROJECT_FOLDER).toString();
            storageProperties.setDataDirectory(dataDir);
        }

        dataDirectoryPath = Paths.get(dataDir);

        try {
            if (!Files.exists(this.dataDirectoryPath)) {
                Files.createDirectories(dataDirectoryPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create data directory!");
        }
        this.fileRepository = fileRepository;
    }

    @Override
    public File saveFileWithMetadata(MultipartFile file, String relativePath, User user,
                                     String customFilename, boolean preserveExtension) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty!");
        }

        String safePath = validateAndNormalizePath(relativePath);
        Path destinationPath = resolveDirectoryPath(safePath);
        Files.createDirectories(destinationPath);

        String safeFilename = customFilename != null ?
                customFilename :
                generateSafeFilename(file.getOriginalFilename(), preserveExtension);

        Path destinationFilePath = destinationPath.resolve(safeFilename);
        Files.copy(file.getInputStream(), destinationFilePath, StandardCopyOption.REPLACE_EXISTING);

        File fileMetadata = new File();
        fileMetadata.setFilename(safeFilename);
        fileMetadata.setOriginalFilename(file.getOriginalFilename());
        fileMetadata.setFileSize(file.getSize());
        fileMetadata.setContentType(file.getContentType());
        fileMetadata.setFilePath(safePath + "/" + safeFilename);
        fileMetadata.setMd5Sum(calculateHash(file));
        fileMetadata.setUser(user);

        return fileRepository.save(fileMetadata);
    }

    @Override
    public String generateSafeFilename(String originalFilename, boolean preserveExtension) {
        String extension = "";

        if (preserveExtension && originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String validateAndNormalizePath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return "";
        }

        String normalized = Paths.get(relativePath).normalize().toString().replace('\\', '/');

        if (normalized.contains("..") || !SAFE_PATH_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid or unsafe path: " + relativePath);
        }

        return normalized.startsWith("/") ? normalized.substring(1) : normalized;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path resolveAbsolutePath(String relativePath) {
        String safePath = validateAndNormalizePath(relativePath);
        return dataDirectoryPath.resolve(safePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculateHash(MultipartFile file) {
        try {
            return DigestUtils.md5DigestAsHex(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to calculate hash!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFile(File file) {
        if (file == null) {
            return false;
        }

        boolean fileDeleted = deleteFileFromFilesystem(file.getFilePath());

        // Ignoring status, assuming that the metadata no longer has an association in the filesystem.
        if (file.getUser() != null) {
            file.getUser().getFiles().remove(file);
        }
        fileRepository.delete(file);

        return fileDeleted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFileFromFilesystem(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        Path path = Paths.get(storageProperties.getDataDirectory(), filePath);

        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException ignored) {
                throw new StorageException("Could not delete file: " + filePath);
            }
            return true;
        }
        return false;
    }

    private Path resolveDirectoryPath(String relativePath) {
        return relativePath.isEmpty() ? dataDirectoryPath : dataDirectoryPath.resolve(relativePath);
    }
}
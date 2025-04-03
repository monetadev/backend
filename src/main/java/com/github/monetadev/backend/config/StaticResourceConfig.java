package com.github.monetadev.backend.config;

import com.github.monetadev.backend.config.prop.FileProperties;
import com.github.monetadev.backend.config.prop.StorageProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    private final StorageProperties storageProperties;
    private final FileProperties fileProperties;

    @Autowired
    public StaticResourceConfig(StorageProperties storageProperties, FileProperties fileProperties) {
        this.storageProperties = storageProperties;
        this.fileProperties = fileProperties;
    }


    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        String profilePictureDirectory = Paths.get(
                storageProperties.getDataDirectory(),
                fileProperties.getProfilePictureDirName()
        ).toString();

        registry.addResourceHandler("/" + fileProperties.getProfilePictureDirName() + "/**")
                .addResourceLocations("file:" + profilePictureDirectory + "/");
    }
}

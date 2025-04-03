package com.github.monetadev.backend.config.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "moneta.config.file")
public class FileProperties {
    private String profilePictureDirName = "profile";
    private String profilePictureMimeType = "image/jpeg,image/png,image/gif,image/bmp,image/webp";
    private long profilePictureMaxSize = 2097152;
}

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
    private long profilePictureMaxSize = 2097152; // 2MB

    private String documentDirName = "docs";
    private String documentMimeType = "application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation,application/vnd.oasis.opendocument.text,application/vnd.oasis.opendocument.spreadsheet,application/vnd.oasis.opendocument.presentation,text/plain,text/csv,text/html,application/rtf,application/xml,application/json";
}

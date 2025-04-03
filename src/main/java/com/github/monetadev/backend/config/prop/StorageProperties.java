package com.github.monetadev.backend.config.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "moneta.config.storage")
public class StorageProperties {
    private String dataDirectory;
    private Integer storageCacheExpiry = 86400;
}

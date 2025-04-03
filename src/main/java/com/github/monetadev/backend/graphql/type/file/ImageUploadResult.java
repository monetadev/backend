package com.github.monetadev.backend.graphql.type.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ImageUploadResult {
    String filename;
    String originalFilename;
    long size;
    String contentType;
    String path;
}

package com.github.monetadev.backend.graphql.type.ai.set;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FlashcardGenOptions {
    String query;
    MultipartFile referenceFile;
    GenerationType generationType = GenerationType.VERBOSE;
    int kQuestions;
}

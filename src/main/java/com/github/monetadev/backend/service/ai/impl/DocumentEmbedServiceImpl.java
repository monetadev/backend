package com.github.monetadev.backend.service.ai.impl;

import com.github.monetadev.backend.model.File;
import com.github.monetadev.backend.service.ai.DocumentEmbedService;
import com.github.monetadev.backend.service.file.FileService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentEmbedServiceImpl implements DocumentEmbedService {
    private final QdrantVectorStore vectorStore;
    private final FileService fileService;
    private final AuthenticationService authenticationService;

    public DocumentEmbedServiceImpl(QdrantVectorStore vectorStore, FileService fileService, AuthenticationService authenticationService) {
        this.vectorStore = vectorStore;
        this.fileService = fileService;
        this.authenticationService = authenticationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String embedFile(File file) {
        Resource resource = fileService.getResourceByFile(file);
        if (file.getContentType().equals("application/pdf")) {
            return embedFileByDocumentReader(file, resource, DocumentType.STRUCTURED_PDF);
        }
        return embedFileByDocumentReader(file, resource, DocumentType.OTHER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEmbeddingById(UUID id) {
        Filter.Expression expression = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key("docId"),
                new Filter.Value(id.toString())
        );
        vectorStore.delete(expression);
    }

    private String embedFileByDocumentReader(File file, Resource fileResource, DocumentType type) {
        List<Document> documents = switch (type) {
            case OTHER, PDF -> {
                TikaDocumentReader tika = new TikaDocumentReader(fileResource);
                yield tika.get();
            }
            case STRUCTURED_PDF -> {
                ParagraphPdfDocumentReader paragraphPdf = new ParagraphPdfDocumentReader(fileResource, PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
                yield paragraphPdf.get();
            }
        };
        documents.forEach(document -> {
            document.getMetadata().put("docId", file.getId().toString());
            document.getMetadata().put("originalFilename", file.getOriginalFilename());
            document.getMetadata().put("filename", file.getFilename());
            document.getMetadata().put("userId", authenticationService.getAuthenticatedUser().getId().toString());
        });
        vectorStore.accept(new TokenTextSplitter().apply(documents));
        return file.getOriginalFilename();
    }

    private enum DocumentType {
        STRUCTURED_PDF,
        PDF,
        OTHER
    }
}

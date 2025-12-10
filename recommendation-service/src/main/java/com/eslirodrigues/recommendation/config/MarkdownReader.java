package com.eslirodrigues.recommendation.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarkdownReader {

    private final Resource resource;

    public MarkdownReader(@Value("classpath:document/rag-text.md") Resource resource) {
        this.resource = resource;
    }

    public List<Document> loadMarkdown() {
        var config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(false)
                .withIncludeBlockquote(true)
                .withIncludeCodeBlock(true)
                .build();

        var reader = new MarkdownDocumentReader(this.resource, config);
        return reader.get();
    }
}
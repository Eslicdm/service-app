package com.eslirodrigues.recommendation.service;

import com.eslirodrigues.recommendation.config.MarkdownReader;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final MarkdownReader markdownReader;

    @Value("classpath:/prompts/rag-prompt.md")
    private Resource ragPrompt;

    public RagService(
            VectorStore vectorStore,
            ChatClient.Builder chatClient,
            MarkdownReader markdownReader
    ) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient.build();
        this.markdownReader = markdownReader;
    }

    @PostConstruct
    public void init() {
        try {
            // Check if the documents have already been loaded by searching for a known keyword.
            List<Document> existingDocs = vectorStore.similaritySearch(
                    SearchRequest.builder().query("Garden Pass").topK(1).build()
            );
            if (!existingDocs.isEmpty()) {
                log.info("VectorStore already contains documents. Skipping data loading.");
                return;
            }
        } catch (IllegalArgumentException e) {
            log.info("VectorStore collection not found. Error: {}", e.getMessage());
        }

        List<Document> documents = markdownReader.loadMarkdown();
        if (!documents.isEmpty()) {
            vectorStore.add(documents);
            log.info("Loaded {} documents into VectorStore", documents.size());
        } else {
            log.warn("No new documents found to load.");
        }
    }

    public String generateAnswer(String userQuery) {
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(userQuery).topK(50).build()
        );

        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        log.info("Context: {}", context);

        var promptTemplate = new PromptTemplate(ragPrompt);
        var prompt = promptTemplate.create(Map.of(
                "context", context,
                "question", userQuery
        ));

        return chatClient.prompt(prompt).call().content();
    }
}
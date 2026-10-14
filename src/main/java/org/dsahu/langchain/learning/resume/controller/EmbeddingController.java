package org.dsahu.langchain.learning.resume.controller;

import org.dsahu.langchain.learning.resume.dto.EmbeddingRequest;
import org.dsahu.langchain.learning.resume.dto.EmbeddingResponse;
import org.dsahu.langchain.learning.resume.service.CandidateEmbeddingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.langchain4j.data.embedding.Embedding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/resumes/embeddings")
@RequiredArgsConstructor
@Slf4j
public class EmbeddingController {

    private final CandidateEmbeddingService candidateEmbeddingService;

    @PostMapping
    public EmbeddingResponse generateEmbedding(
            @RequestBody EmbeddingRequest request) {

        log.info("Generating embedding through REST API");

        Embedding embedding =
                candidateEmbeddingService.generateEmbedding(request.text());

        return new EmbeddingResponse(
                embedding.dimension(),
                embedding.vectorAsList()
        );
    }
}
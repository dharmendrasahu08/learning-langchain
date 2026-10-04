package org.dsahu.langchain.learning.resume.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dsahu.langchain.learning.resume.entity.ResumeDocument;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateEmbeddingServiceImpl
        implements CandidateEmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final CandidateSearchTextBuilder searchTextBuilder;

    @Override
    public Embedding createEmbedding(
            ResumeDocument.CandidateProfile profile) {
        log.debug("createEmbedding#Creating embedding for candidate: {}",
                profile.getName());

        String searchableText = searchTextBuilder.build(profile);

        Embedding embedding = embeddingModel.embed(searchableText).content();

        log.debug("createEmbedding#Embedding created successfully for candidate: {}, dimensions: {}",
                profile.getName(),
                embedding.dimension());

        return embedding;
    }
    
    @Override
    public Embedding generateEmbedding(String text) {
        log.info("generateEmbedding#Generating embedding for text");
        return embeddingModel.embed(text).content();
    }
    
    @Override
    public Embedding createQueryEmbedding(String query) {
        log.debug("generateEmbedding#Creating embedding for search query: {}", query);
        Embedding embedding =
                embeddingModel.embed(query).content();
        log.debug("generateEmbedding#Query embedding created successfully, dimensions: {}",
                embedding.dimension());
        return embedding;
    }
}
package org.dsahu.langchain.learning.resume.service;

import dev.langchain4j.data.embedding.Embedding;
import org.dsahu.langchain.learning.resume.entity.ResumeDocument;

public interface CandidateEmbeddingService {

    Embedding createEmbedding(ResumeDocument.CandidateProfile profile);
    Embedding generateEmbedding(String text);
    Embedding createQueryEmbedding(String query);
}
package org.dsahu.langchain.learning.resume.service;

import java.util.List;
import java.util.UUID;

import org.dsahu.langchain.learning.resume.dto.ResumeCreateRequest;
import org.dsahu.langchain.learning.resume.dto.ResumeResponse;
import org.dsahu.langchain.learning.resume.dto.ResumeSearchRequest;

import dev.langchain4j.data.embedding.Embedding;

public interface ResumeService {

    ResumeResponse create(ResumeCreateRequest request);
    List<ResumeResponse> findAll();
    ResumeResponse findByProfileId(String profileId);
    List<ResumeResponse> search(ResumeSearchRequest request);
    Embedding createEmbedding(String id);
}
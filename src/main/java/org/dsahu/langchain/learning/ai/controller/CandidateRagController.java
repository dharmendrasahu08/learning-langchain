package org.dsahu.langchain.learning.ai.controller;

import java.util.List;

import org.dsahu.langchain.learning.ai.rag.CandidateRagService;
import org.dsahu.langchain.learning.resume.dto.CandidateSearchResponse;
import org.dsahu.langchain.learning.resume.service.CandidateQdrantService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumes/rag")
@RequiredArgsConstructor
public class CandidateRagController {

    private final CandidateQdrantService candidateQdrantService;
    private final CandidateRagService candidateRagService;

    @GetMapping("/candidates")
    public String searchCandidates(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {

        List<CandidateSearchResponse> candidates =
                candidateQdrantService.searchCandidates(query, limit);

        return candidateRagService.answer(query, candidates);
    }
}
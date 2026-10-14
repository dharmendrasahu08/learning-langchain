package org.dsahu.langchain.learning.ai.rag;

import java.util.List;

import org.dsahu.langchain.learning.resume.dto.CandidateSearchResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateRagService {

    private final CandidateContextBuilder contextBuilder;
    private final CandidateRagAssistant candidateRagAssistant;

    public String answer(String query, List<CandidateSearchResponse> candidates) {

        log.info("Generating RAG answer. Query: {}, candidates: {}",
                query, candidates.size());

        String context = contextBuilder.build(candidates);

        String userMessage = """
                Candidate Context:
                
                %s
                
                User Question:
                
                %s
                """.formatted(context, query);

        String answer =  candidateRagAssistant.answer(userMessage);
        return answer
                .replaceAll("\\s*,\\s*", ", ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
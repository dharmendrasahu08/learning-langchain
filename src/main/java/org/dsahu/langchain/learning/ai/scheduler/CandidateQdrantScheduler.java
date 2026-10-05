package org.dsahu.langchain.learning.ai.scheduler;

import org.dsahu.langchain.learning.resume.service.CandidateQdrantService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CandidateQdrantScheduler {

    private final CandidateQdrantService candidateQdrantService;

    @Scheduled(fixedDelay = 300000)
    public void syncCandidates() {

        log.info("Starting candidate Qdrant synchronization");

        candidateQdrantService.syncCandidatesToQdrant();

        log.info("Candidate Qdrant synchronization completed");
    }
}
package org.dsahu.langchain.learning.resume.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import io.qdrant.client.QdrantClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QdrantConnectionService {

    private final QdrantClient qdrantClient;

    public void verifyConnection() {
        log.info("verifyConnection#Verifying connection to Qdrant");
        try {
            List<String> collections =
                    qdrantClient.listCollectionsAsync().get();

            log.info("verifyConnection#Qdrant connection successful. Collections: {}",
                    collections);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "verifyConnection#Interrupted while connecting to Qdrant", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(
                    "verifyConnection#Failed to connect to Qdrant", e);
        }
    }
}
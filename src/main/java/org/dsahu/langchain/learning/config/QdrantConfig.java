package org.dsahu.langchain.learning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class QdrantConfig {
	
    @Value("${qdrant.host}")
    private String host;

    @Value("${qdrant.grpc-port}")
    private int grpcPort;

    @Bean
    QdrantClient qdrantClient() {
    	log.info("Creating Qdrant client: {}:{}", host, grpcPort);

        QdrantGrpcClient grpcClient = QdrantGrpcClient
                .newBuilder(host, grpcPort, false)
                .build();

        return new QdrantClient(grpcClient);
    }
}
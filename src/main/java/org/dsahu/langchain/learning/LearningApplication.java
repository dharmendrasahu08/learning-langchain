package org.dsahu.langchain.learning;

import org.dsahu.langchain.learning.resume.service.CandidateQdrantService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LearningApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningApplication.class, args);
	}
	
	/*
	 * It iss only for testing purpose to check connectivity between Qdrant and springboot*/
	/*
	@Bean
    CommandLineRunner verifyQdrantConnection(
            QdrantConnectionService qdrantConnectionService) {
        return args -> qdrantConnectionService.verifyConnection();
    }
    */
	
	/*
	 * This is one time activity for learning purpose,
	 * letter we will creation from schedular reading collection from mongoDB
	 */
	/*
	@Bean
	CommandLineRunner createQdrantCollection(
	        CandidateQdrantService candidateQdrantService) {
	    return args -> candidateQdrantService.createCollection();
	}*/

}

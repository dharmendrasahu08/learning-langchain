package org.dsahu.langchain.learning.resume.service;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.QueryFactory.nearest;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dsahu.langchain.learning.resume.dto.CandidateSearchRequest;
import org.dsahu.langchain.learning.resume.dto.CandidateSearchResponse;
import org.dsahu.langchain.learning.resume.entity.ResumeDocument;
import org.dsahu.langchain.learning.resume.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import dev.langchain4j.data.embedding.Embedding;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.QueryPoints;
import io.qdrant.client.grpc.Points.ScoredPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static io.qdrant.client.ConditionFactory.matchKeyword;
import io.qdrant.client.grpc.Common.Filter;


@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateQdrantServiceImpl
        implements CandidateQdrantService {

    private static final String COLLECTION_NAME = "candidate_profiles";
    private static final int VECTOR_SIZE = 1536;

    private final QdrantClient qdrantClient;
    private final CandidateEmbeddingService candidateEmbeddingService;
    private final ResumeRepository resumeRepository;

    @Override
    public void batchUpsertCandidates(
            List<ResumeDocument> documents) {

        log.info(
                "Starting batch upsert of {} candidates into Qdrant",
                documents.size());

        if (documents.isEmpty()) {
            log.info("No candidates available for batch upsert");
            return;
        }

        List<PointStruct> points = documents.stream()
                .filter(document -> document.getProfile() != null)
                .map(this::buildCandidatePoint)
                .toList();

        try {

            qdrantClient.upsertAsync(
                    COLLECTION_NAME,
                    points
            ).get();

            log.info(
                    "Batch upsert completed successfully. Candidates upserted: {}",
                    points.size());

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Interrupted while batch upserting candidates",
                    e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "Failed to batch upsert candidates",
                    e);
        }
    }
    
    private PointStruct buildCandidatePoint(
            ResumeDocument document) {

        String profileId = document.getProfileId();

        ResumeDocument.CandidateProfile profile =
                document.getProfile();

        Embedding embedding =
                candidateEmbeddingService.createEmbedding(profile);

        UUID qdrantPointId =
                UUID.nameUUIDFromBytes(
                        profileId.getBytes(StandardCharsets.UTF_8));

        return PointStruct.newBuilder()
                .setId(id(qdrantPointId))
                .setVectors(vectors(embedding.vectorAsList()))
                .putAllPayload(Map.of(
                        "profileId", value(profileId),
                        "name", value(profile.getName()),
                        "profileType", value(profile.getProfileType()),
                        "country", value(profile.getCountry()),
                        "location", value(profile.getLocation())
                ))
                .build();
    }
    
    @Override
    public void syncCandidatesToQdrant() {

        log.info("Starting candidate synchronization from MongoDB to Qdrant");

        List<ResumeDocument> documents =
                resumeRepository.findByIsSyncedToVectorDbFalse();

        log.info(
                "Candidates found in MongoDB: {}",
                documents.size());

        batchUpsertCandidates(documents);

        log.info(
                "Candidate synchronization completed");
    }
    
    @Override
    public void createCollection() {
        log.info("createCollection#Creating Qdrant collection: {}", COLLECTION_NAME);
        try {
            qdrantClient.createCollectionAsync(
                    COLLECTION_NAME,
                    VectorParams.newBuilder()
                            .setSize(VECTOR_SIZE)
                            .setDistance(Distance.Cosine)
                            .build()
            ).get();

            log.info("createCollection#Qdrant collection created successfully: {}",
                    COLLECTION_NAME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "createCollection#Interrupted while creating Qdrant collection", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(
                    "createCollection#Failed to create Qdrant collection: " + COLLECTION_NAME, e);
        }
        log.info("Qdrant collection created successfully: {}",
                COLLECTION_NAME);
    }
    
    @Override
    public void upsertCandidate(
            String profileId,
            ResumeDocument.CandidateProfile profile) {

        log.info("Upserting candidate into Qdrant. profileId: {}",
                profileId);

        Embedding embedding =
                candidateEmbeddingService.createEmbedding(profile);

        UUID qdrantPointId =
                UUID.nameUUIDFromBytes(
                        profileId.toString()
                                .getBytes(StandardCharsets.UTF_8));

        PointStruct point = PointStruct.newBuilder()
                .setId(id(qdrantPointId))
                .setVectors(vectors(embedding.vectorAsList()))
                .putAllPayload(Map.of(
                        "profileId", value(profileId.toString()),
                        "name", value(profile.getName()),
                        "profileType", value(profile.getProfileType()),
                        "country", value(profile.getCountry()),
                        "location", value(profile.getLocation())
                ))
                .build();

        try {

            qdrantClient.upsertAsync(
                    COLLECTION_NAME,
                    List.of(point)
            ).get();

            log.info(
                    "Candidate successfully upserted into Qdrant. profileId: {}",
                    profileId);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Interrupted while upserting candidate: "
                            + profileId,
                    e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "Failed to upsert candidate: "
                            + profileId,
                    e);
        }
    }
    
	@Override
	public List<CandidateSearchResponse> searchCandidates(String query, int limit) {

		log.info("Searching candidates in Qdrant. Query: {}, limit: {}", query, limit);

		Embedding queryEmbedding = candidateEmbeddingService.createQueryEmbedding(query);

		try {

			QueryPoints queryPoints = QueryPoints.newBuilder().setCollectionName(COLLECTION_NAME)
					.setQuery(nearest(queryEmbedding.vectorAsList())).setLimit(limit).setWithPayload(enable(true))
					.build();

			List<ScoredPoint> results =
			        qdrantClient.queryAsync(queryPoints).get();

			log.info(
			        "Qdrant search completed. Results found: {}",
			        results.size());

			return buildSearchResponses(results);

		} catch (InterruptedException e) {

			Thread.currentThread().interrupt();

			throw new IllegalStateException("Interrupted while searching candidates", e);

		} catch (ExecutionException e) {

			throw new IllegalStateException("Failed to search candidates", e);
		}
	}
	
	private List<CandidateSearchResponse> buildSearchResponses(List<ScoredPoint> results) {

		if (results.isEmpty()) {
			return List.of();
		}

		List<String> profileIds = results.stream().map(this::extractProfileId).toList();
		List<ResumeDocument> documents = resumeRepository.findByProfileIdIn(profileIds);
		Map<String, ResumeDocument> documentsByProfileId = documents
				.stream()
				.collect(
						Collectors
						.toMap(ResumeDocument::getProfileId, Function.identity())
						);
		return results
				.stream()
				.map(result -> toSearchResponse(result, documentsByProfileId))
				.toList();
	}
	
	private String extractProfileId(
			ScoredPoint result) {
		return result.getPayloadOrThrow("profileId").getStringValue();
	}

	private CandidateSearchResponse toSearchResponse(ScoredPoint result,
			Map<String, ResumeDocument> documentsByProfileId) {

		String profileId = extractProfileId(result);

		ResumeDocument document = documentsByProfileId.get(profileId);

		if (document == null) {
			log.warn("Resume not found in MongoDB for profileId: {}", profileId);

			return null;
		}

		return CandidateSearchResponse.builder()
		        .profileId(profileId)
		        .name(document.getProfile().getName())
		        .profileType(document.getProfile().getProfileType())
		        .profileSummary(document.getProfile().getProfileSummary())
		        .yearsOfExperience(document.getProfile().getYearsOfExperience())
		        .skills(document.getProfile().getSkills())
		        .domains(document.getProfile().getDomains())
		        .pastCompanies(document.getProfile().getPastCompanies())
		        .location(document.getProfile().getLocation())
		        .country(document.getProfile().getCountry())
		        .qualification(document.getProfile().getQualification())
		        .specialization(document.getProfile().getSpecialization())
		        .institute(document.getProfile().getInstitute())
		        .certifications(document.getProfile().getCertifications())
		        .score(result.getScore())
		        .build();
	}
	
	@Override
	public List<CandidateSearchResponse> searchCandidates(
	        CandidateSearchRequest request) {

	    log.info(
	            "Searching candidates in Qdrant. Query: {}, country: {}, limit: {}",
	            request.query(),
	            request.country(),
	            request.limit());

	    Embedding queryEmbedding =
	            candidateEmbeddingService.createQueryEmbedding(
	                    request.query());

	    try {
	        QueryPoints.Builder queryBuilder =
	                QueryPoints.newBuilder()
	                        .setCollectionName(COLLECTION_NAME)
	                        .setQuery(nearest(queryEmbedding.vectorAsList()))
	                        .setLimit(request.limit())
	                        .setWithPayload(enable(true));

	        // Apply country filter only when provided
	        if (request.country() != null
	                && !request.country().isBlank()) {

	            Filter filter = Filter.newBuilder()
	                    .addMust(
	                            matchKeyword(
	                                    "country",
	                                    request.country()))
	                    .addMust(
	                            matchKeyword(
	                                    "profileType",
	                                    request.profileType()))
	                    .build();

	            queryBuilder.setFilter(filter);

	            log.info(
	                    "Applying Qdrant country filter: {}",
	                    request.country());
	        }

	        // Execute Qdrant search
	        List<ScoredPoint> results =
	                qdrantClient
	                        .queryAsync(queryBuilder.build())
	                        .get();

	        log.info(
	                "Qdrant search completed. Results found: {}",
	                results.size());

	        return buildSearchResponses(results);

	    } catch (InterruptedException e) {

	        Thread.currentThread().interrupt();

	        throw new IllegalStateException(
	                "Interrupted while searching candidates",
	                e);

	    } catch (ExecutionException e) {

	        throw new IllegalStateException(
	                "Failed to search candidates",
	                e);
	    }
	}
	
}
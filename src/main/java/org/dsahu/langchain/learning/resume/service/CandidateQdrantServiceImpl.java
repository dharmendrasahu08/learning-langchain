
package org.dsahu.langchain.learning.resume.service;

import static io.qdrant.client.ConditionFactory.filter;
import static io.qdrant.client.ConditionFactory.matchKeyword;
import static io.qdrant.client.ConditionFactory.range;
import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.QueryFactory.nearest;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorFactory.vector;
import static io.qdrant.client.VectorsFactory.namedVectors;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import io.qdrant.client.grpc.Collections.CreateCollection;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.Modifier;
import io.qdrant.client.grpc.Collections.SparseVectorConfig;
import io.qdrant.client.grpc.Collections.SparseVectorParams;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Collections.VectorsConfig;
import io.qdrant.client.grpc.Common.Filter;
import io.qdrant.client.grpc.Common.Range;
import io.qdrant.client.grpc.Points.Document;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.QueryPoints;
import io.qdrant.client.grpc.Points.ScoredPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateQdrantServiceImpl
        implements CandidateQdrantService {

    private static final String COLLECTION_NAME = "candidate_profiles";
    private static final int VECTOR_SIZE = 1536;

    private static final String DENSE_VECTOR_NAME = "dense";
    private static final String SPARSE_VECTOR_NAME = "bm25";
    private static final String BM25_MODEL = "qdrant/bm25";

    private final QdrantClient qdrantClient;
    private final CandidateEmbeddingService candidateEmbeddingService;
    private final ResumeRepository resumeRepository;

    // Index candidates in batches. Each point contains dense and BM25 vectors.
    @Override
    public void batchUpsertCandidates(List<ResumeDocument> documents) {

        log.info("Starting candidate batch upsert. Count: {}", documents.size());

        List<PointStruct> points = documents.stream()
                .filter(document -> document.getProfile() != null)
                .map(this::buildCandidatePoint)
                .toList();

        if (points.isEmpty()) {
            log.info("No candidates with profiles available for upsert");
            return;
        }

        try {
            qdrantClient.upsertAsync(COLLECTION_NAME, points).get();

            log.info("Batch upsert completed. Points upserted: {}", points.size());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while batch upserting candidates", e);

        } catch (ExecutionException e) {
            throw new IllegalStateException(
                    "Failed to batch upsert candidates", e);
        }
    }

    // Build one point with both the semantic and keyword-search vectors.
    private PointStruct buildCandidatePoint(ResumeDocument document) {

        String profileId = document.getProfileId();
        ResumeDocument.CandidateProfile profile = document.getProfile();

        Embedding embedding = candidateEmbeddingService.createEmbedding(profile);
        String candidateText = buildCandidateText(profile);

        UUID pointId = UUID.nameUUIDFromBytes(
                profileId.getBytes(StandardCharsets.UTF_8));

        return PointStruct.newBuilder()
                .setId(id(pointId))
                .setVectors(namedVectors(Map.of(
                        DENSE_VECTOR_NAME,
                        vector(embedding.vectorAsList()),

                        SPARSE_VECTOR_NAME,
                        vector(Document.newBuilder()
                                .setText(candidateText)
                                .setModel(BM25_MODEL)
                                .build())
                )))
                .putAllPayload(Map.of(
                        "profileId", value(profileId),
                        "name", value(profile.getName()),
                        "profileType", value(profile.getProfileType()),
                        "country", value(profile.getCountry()),
                        "location", value(profile.getLocation()),
                        "yearsOfExperience", value(
                                profile.getYearsOfExperience())
                ))
                .build();
    }
    

    // Text used by BM25. Include fields recruiters are likely to search.
    private String buildCandidateText(
            ResumeDocument.CandidateProfile profile) {

        StringBuilder text = new StringBuilder();

        appendText(text, profile.getName());
        appendText(text, profile.getProfileType());
        appendText(text, profile.getProfileSummary());
        appendText(text, profile.getYearsOfExperience());
        appendText(text, profile.getSkills());
        appendText(text, profile.getDomains());
        appendText(text, profile.getPastCompanies());
        appendText(text, profile.getLocation());
        appendText(text, profile.getCountry());
        appendText(text, profile.getQualification());
        appendText(text, profile.getSpecialization());
        appendText(text, profile.getInstitute());
        appendText(text, profile.getCertifications());
        appendText(text, profile.getAppliedPosition());

        return text.toString();
    }

    private void appendText(StringBuilder text, Object field) {
        if (field != null && !field.toString().isBlank()) {
            text.append(field).append(' ');
        }
    }

    // Create a collection with named dense and sparse vectors.
    @Override
    public void createCollection() {

        log.info("Creating Qdrant collection: {}", COLLECTION_NAME);

        try {
            CreateCollection request = CreateCollection.newBuilder()
                    .setCollectionName(COLLECTION_NAME)
                    .setVectorsConfig(
                    	    VectorsConfig.newBuilder()
                    	        .setParams(
                    	            VectorParams.newBuilder()
                    	                .setSize(VECTOR_SIZE)
                    	                .setDistance(Distance.Cosine)
                    	                .build())
                    	        .build())
                    .setSparseVectorsConfig(
                            SparseVectorConfig.newBuilder()
                                    .putMap(
                                            SPARSE_VECTOR_NAME,
                                            SparseVectorParams.newBuilder()
                                                    .setModifier(Modifier.Idf)
                                                    .build())
                                    .build())
                    .setOnDiskPayload(true)
                    .build();

            qdrantClient.createCollectionAsync(request).get();

            log.info("Qdrant collection created: {}", COLLECTION_NAME);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while creating Qdrant collection", e);

        } catch (ExecutionException e) {
            throw new IllegalStateException(
                    "Failed to create Qdrant collection: "
                            + COLLECTION_NAME, e);
        }
    }

    @Override
    public void upsertCandidate(
            String profileId,
            ResumeDocument.CandidateProfile profile) {

        log.info("Upserting candidate. profileId: {}", profileId);

        UUID pointId = UUID.nameUUIDFromBytes(
                profileId.getBytes(StandardCharsets.UTF_8));

        // Reuse the same logic as batch upsert.
        ResumeDocument document = new ResumeDocument();
        document.setProfileId(profileId);
        document.setProfile(profile);

        PointStruct point = buildCandidatePoint(document);

        try {
            qdrantClient.upsertAsync(
                    COLLECTION_NAME, List.of(point)).get();

            log.info("Candidate upserted successfully. profileId: {}", profileId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while upserting candidate: " + profileId, e);

        } catch (ExecutionException e) {
            throw new IllegalStateException(
                    "Failed to upsert candidate: " + profileId, e);
        }
    }

    @Override
    public void syncCandidatesToQdrant() {

        log.info("Starting candidate synchronization from MongoDB");

        List<ResumeDocument> documents =
                resumeRepository.findByIsSyncedToVectorDbFalse();

        log.info("Unsynced candidates found: {}", documents.size());

        batchUpsertCandidates(documents);

        log.info("Candidate synchronization finished");
    }

    // Existing dense semantic search.
    @Override
    public List<CandidateSearchResponse> searchCandidates(
            String query, int limit) {

        log.info("Dense search. Query: {}, limit: {}", query, limit);

        Embedding queryEmbedding =
                candidateEmbeddingService.createQueryEmbedding(query);

        try {
            QueryPoints request = QueryPoints.newBuilder()
                    .setCollectionName(COLLECTION_NAME)
                    .setQuery(nearest(queryEmbedding.vectorAsList()))
                    .setUsing(DENSE_VECTOR_NAME)
                    .setLimit(limit)
                    .setWithPayload(enable(true))
                    .build();

            List<ScoredPoint> results =
                    qdrantClient.queryAsync(request).get();

            return buildSearchResponses(results);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while searching candidates", e);

        } catch (ExecutionException e) {
            throw new IllegalStateException(
                    "Failed to search candidates", e);
        }
    }

    // Existing dense search with metadata filters.
    @Override
    public List<CandidateSearchResponse> searchCandidates(
            CandidateSearchRequest request) {

        log.info(
                "Dense candidate search. Query: {}, country: {}, profileType: {}",
                request.query(), request.country(), request.profileType());

        Embedding queryEmbedding =
                candidateEmbeddingService.createQueryEmbedding(request.query());

        try {
            QueryPoints.Builder queryBuilder = QueryPoints.newBuilder()
                    .setCollectionName(COLLECTION_NAME)
                    .setQuery(nearest(queryEmbedding.vectorAsList()))
                    .setUsing(DENSE_VECTOR_NAME)
                    .setLimit(request.limit())
                    .setWithPayload(enable(true));

            Filter.Builder filterBuilder = Filter.newBuilder();
            boolean hasFilter = false;

            if (request.country() != null && !request.country().isBlank()) {
                filterBuilder.addMust(
                        matchKeyword("country", request.country()));
                hasFilter = true;
            }

            // Support one profile type or comma-separated types, e.g. HR,BA.
            if (request.profileType() != null
                    && !request.profileType().isBlank()) {

                Filter.Builder profileTypeFilter = Filter.newBuilder();

                Arrays.stream(request.profileType().split(","))
                        .map(String::trim)
                        .filter(type -> !type.isBlank())
                        .forEach(type ->
                                profileTypeFilter.addShould(
                                        matchKeyword("profileType", type)));

                filterBuilder.addMust(filter(profileTypeFilter.build()));
                hasFilter = true;
            }

            if (request.minExperience() != null
                    || request.maxExperience() != null) {

                Range.Builder experienceRange = Range.newBuilder();

                if (request.minExperience() != null) {
                    experienceRange.setGte(
                            request.minExperience().doubleValue());
                }

                if (request.maxExperience() != null) {
                    experienceRange.setLte(
                            request.maxExperience().doubleValue());
                }

                filterBuilder.addMust(
                        range("yearsOfExperience", experienceRange.build()));
                hasFilter = true;
            }

            if (hasFilter) {
                queryBuilder.setFilter(filterBuilder.build());
            }

            List<ScoredPoint> results =
                    qdrantClient.queryAsync(queryBuilder.build()).get();

            log.info("Dense search results found: {}", results.size());

            return buildSearchResponses(results);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while searching candidates", e);

        } catch (ExecutionException e) {
            throw new IllegalStateException(
                    "Failed to search candidates", e);
        }
    }

    private List<CandidateSearchResponse> buildSearchResponses(
            List<ScoredPoint> results) {

        if (results.isEmpty()) {
            return List.of();
        }

        List<String> profileIds = results.stream()
                .map(this::extractProfileId)
                .toList();

        List<ResumeDocument> documents =
                resumeRepository.findByProfileIdIn(profileIds);

        Map<String, ResumeDocument> documentsByProfileId = documents.stream()
                .collect(Collectors.toMap(
                        ResumeDocument::getProfileId,
                        Function.identity(),
                        (first, second) -> first));

        return results.stream()
                .map(result -> toSearchResponse(result, documentsByProfileId))
                .filter(response -> response != null)
                .toList();
    }

    private String extractProfileId(ScoredPoint result) {
        return result.getPayloadOrThrow("profileId").getStringValue();
    }

    private CandidateSearchResponse toSearchResponse(
            ScoredPoint result,
            Map<String, ResumeDocument> documentsByProfileId) {

        String profileId = extractProfileId(result);
        ResumeDocument document = documentsByProfileId.get(profileId);

        if (document == null || document.getProfile() == null) {
            log.warn("Resume not found in MongoDB. profileId: {}", profileId);
            return null;
        }

        ResumeDocument.CandidateProfile profile = document.getProfile();

        return CandidateSearchResponse.builder()
                .profileId(profileId)
                .name(profile.getName())
                .profileType(profile.getProfileType())
                .profileSummary(profile.getProfileSummary())
                .yearsOfExperience(profile.getYearsOfExperience())
                .skills(profile.getSkills())
                .domains(profile.getDomains())
                .pastCompanies(profile.getPastCompanies())
                .location(profile.getLocation())
                .country(profile.getCountry())
                .qualification(profile.getQualification())
                .specialization(profile.getSpecialization())
                .institute(profile.getInstitute())
                .certifications(profile.getCertifications())
                .score(result.getScore())
                .build();
    }
    

	@Override
	public List<CandidateSearchResponse> searchCandidatesBm25(String query, int limit) {

		log.info("BM25 search started. Query: {}, limit: {}", query, limit);

		try {
			QueryPoints queryPoints = QueryPoints.newBuilder()
					.setCollectionName(COLLECTION_NAME)
					.setQuery(nearest(Document.newBuilder()
							.setText(query)
							.setModel(BM25_MODEL).build()))
					.setUsing(SPARSE_VECTOR_NAME)
					.setLimit(limit)
					.setWithPayload(enable(true)).build();

			List<ScoredPoint> results = qdrantClient.queryAsync(queryPoints).get();

			log.info("BM25 search completed. Results found: {}", results.size());

			return buildSearchResponses(results);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Interrupted during BM25 search", e);

		} catch (ExecutionException e) {
			throw new IllegalStateException("Failed during BM25 search", e);
		}
	}
	

	@Override
	public List<CandidateSearchResponse> searchCandidatesHybrid(String query, int limit) {

		log.info("Hybrid search started. Query: {}, limit: {}", query, limit);

		// 1. Retrieve candidates using both search strategies.
		List<CandidateSearchResponse> denseResults = searchCandidates(query, limit);

		List<CandidateSearchResponse> bm25Results = searchCandidatesBm25(query, limit);

		// 2. Store candidates and their fused RRF scores.
		Map<String, CandidateSearchResponse> candidatesById = new HashMap<>();

		Map<String, Double> rrfScores = new HashMap<>();

		int rrfConstant = 60;

		// 3. Add dense search rankings.
		addRrfScores(denseResults, rrfConstant, candidatesById, rrfScores);

		// 4. Add BM25 search rankings.
		addRrfScores(bm25Results, rrfConstant, candidatesById, rrfScores);

		// 5. Sort by fused score and return the top candidates.

		List<CandidateSearchResponse> results = new ArrayList<>(candidatesById.values());

		results.sort((first, second) -> Double.compare(rrfScores.get(second.getProfileId()),
				rrfScores.get(first.getProfileId())));

		if (results.size() > limit) {
			results = new ArrayList<>(results.subList(0, limit));
		}
		log.info("Hybrid search completed. Results found: {}", results.size());

		// Return the actual RRF score in the response.
		List<CandidateSearchResponse> finalResults = results.stream()
		        .map(candidate -> {
		            double rrfScore = rrfScores.get(candidate.getProfileId());

		            return CandidateSearchResponse.builder()
		                    .profileId(candidate.getProfileId())
		                    .name(candidate.getName())
		                    .profileType(candidate.getProfileType())
		                    .profileSummary(candidate.getProfileSummary())
		                    .yearsOfExperience(candidate.getYearsOfExperience())
		                    .skills(candidate.getSkills())
		                    .domains(candidate.getDomains())
		                    .pastCompanies(candidate.getPastCompanies())
		                    .location(candidate.getLocation())
		                    .country(candidate.getCountry())
		                    .qualification(candidate.getQualification())
		                    .specialization(candidate.getSpecialization())
		                    .institute(candidate.getInstitute())
		                    .certifications(candidate.getCertifications())
		                    .appliedPosition(candidate.getAppliedPosition())
		                    .score((float) rrfScore)
		                    .build();
		        })
		        .toList();

		log.info("Hybrid search completed. Results found: {}", finalResults.size());

		return finalResults;
	}

	private void addRrfScores(List<CandidateSearchResponse> results, int rrfConstant,
			Map<String, CandidateSearchResponse> candidatesById, Map<String, Double> rrfScores) {

		for (int i = 0; i < results.size(); i++) {
			CandidateSearchResponse candidate = results.get(i);

			if (candidate == null || candidate.getProfileId() == null) {
				continue;
			}

			candidatesById.putIfAbsent(candidate.getProfileId(), candidate);

			// RRF ranks start at 1, not 0.
			double contribution = 1.0 / (rrfConstant + i + 1);

			rrfScores.merge(candidate.getProfileId(), contribution, Double::sum);
		}
	}


}

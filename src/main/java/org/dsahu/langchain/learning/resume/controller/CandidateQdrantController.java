package org.dsahu.langchain.learning.resume.controller;

import java.util.List;

import org.dsahu.langchain.learning.resume.dto.CandidateQdrantRequest;
import org.dsahu.langchain.learning.resume.dto.CandidateSearchResponse;
import org.dsahu.langchain.learning.resume.service.CandidateQdrantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resumes/qdrant/candidates")
@RequiredArgsConstructor
public class CandidateQdrantController {

    private final CandidateQdrantService candidateQdrantService;

	@PostMapping
	public String upsertCandidate(@RequestBody CandidateQdrantRequest request) {

		candidateQdrantService.upsertCandidate(request.profileId(), request.profile());

		return "Candidate upserted successfully: " + request.profileId();
	}
    
	@GetMapping("/search")
	public List<CandidateSearchResponse> searchCandidates(@RequestParam String query,
			@RequestParam(defaultValue = "5") int limit) {

		return candidateQdrantService.searchCandidates(query, limit);
	}
}
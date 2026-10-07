package org.dsahu.langchain.learning.resume.controller;





import java.util.List;

import org.dsahu.langchain.learning.resume.dto.CandidateQdrantRequest;
import org.dsahu.langchain.learning.resume.dto.CandidateSearchRequest;
import org.dsahu.langchain.learning.resume.dto.CandidateSearchResponse;
import org.dsahu.langchain.learning.resume.service.CandidateQdrantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/resumes/qdrant/candidates")
@RequiredArgsConstructor
@Slf4j
public class CandidateQdrantController {

    private final CandidateQdrantService candidateQdrantService;

	@PostMapping
	public String upsertCandidate(@RequestBody CandidateQdrantRequest request) {

		candidateQdrantService.upsertCandidate(request.profileId(), request.profile());

		return "Candidate upserted successfully: " + request.profileId();
	}
    
	//Simple search version-1
	//Request url:http://localhost:8899/api/resumes/qdrant/candidates/search?query=Java%20backend%20developer
	/*@GetMapping("/search")
	public List<CandidateSearchResponse> searchCandidates(@RequestParam String query,
			@RequestParam(defaultValue = "5") int limit) {

		return candidateQdrantService.searchCandidates(query, limit);
	}*/
	
	// Search candidates using semantic vector similarity.
	// Optional country filter is applied directly in Qdrant.
	 @GetMapping("/search")
		public List<CandidateSearchResponse> searchCandidates(@ModelAttribute CandidateSearchRequest request) {

			log.info("Candidate search request received. Query: {}, country: {}, limit: {}", request.query(),
					request.country(), request.limit());

			return candidateQdrantService.searchCandidates(request);
		}
}
package org.dsahu.langchain.learning.ai.rag;

import java.util.List;
import java.util.stream.Collectors;

import org.dsahu.langchain.learning.resume.dto.CandidateSearchResponse;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CandidateContextBuilder {

	public String build(List<CandidateSearchResponse> candidates) {
		log.debug("Building RAG context for {} candidates", candidates.size());
		if (candidates.isEmpty()) {
			return "No matching candidates were found.";
		}
		String context = candidates.stream().map(this::formatCandidate).collect(Collectors.joining("\n\n"));
		log.debug("RAG context built successfully. Context length: {} characters", context.length());
		return context;
	}

	private String formatCandidate(CandidateSearchResponse candidate) {
		return """
				Candidate:
				Name: %s
				Profile Type: %s
				Experience: %s years
				Skills: %s
				Domains: %s
				Previous Companies: %s
				Location: %s
				Country: %s
				Qualification: %s
				Specialization: %s
				Institute: %s
				Certifications: %s
				Applied Position: %s
				Summary: %s
				""".formatted(
						candidate.getName(), 
						candidate.getProfileType(), 
						candidate.getYearsOfExperience(),
				String.join(", ", candidate.getSkills()), String.join(", ", candidate.getDomains()),
				String.join(", ", candidate.getPastCompanies()), candidate.getLocation(), candidate.getCountry(),
				candidate.getQualification(), candidate.getSpecialization(), candidate.getInstitute(),
				String.join(", ", candidate.getCertifications()), 
				candidate.getAppliedPosition(),
				candidate.getProfileSummary());
	}
}
package org.dsahu.langchain.learning.resume.dto;

import org.dsahu.langchain.learning.resume.entity.ResumeDocument;

public record CandidateQdrantRequest(
		String profileId,
        ResumeDocument.CandidateProfile profile
) {
}
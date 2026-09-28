package org.dsahu.langchain.learning.resume.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import java.util.List;

@Builder
public record ResumeResponse(

        String id,

        String originalFileName,

        String storageReference,

        String extractedText,

        CandidateProfileResponse profile,

        LocalDateTime uploadedAt
) {

	@Builder
    public record CandidateProfileResponse(

            String name,

            String email,
            String phoneNum,

            String profileType,

            String profileSummary,

            Double yearsOfExperience,

            List<String> skills,

            List<String> domains,

            List<String> pastCompanies,

            String location,

            String country,

            String qualification,

            String specialization,

            String institute,

            List<String> certifications,

            String appliedPosition,

            LocalDate appliedDate
    ) {
    }
}
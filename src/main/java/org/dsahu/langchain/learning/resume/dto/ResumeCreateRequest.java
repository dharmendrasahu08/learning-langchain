package org.dsahu.langchain.learning.resume.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ResumeCreateRequest(

        @NotBlank
        String originalFileName,

        String storageReference,

        String extractedText,

        @Valid
        @NotNull
        CandidateProfileRequest profile
) {

    public record CandidateProfileRequest(

            @NotBlank
            String name,

            @Email
            String email,
            String phoneNum,

            @NotBlank
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
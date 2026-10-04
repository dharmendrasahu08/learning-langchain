package org.dsahu.langchain.learning.resume.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.dsahu.langchain.learning.common.constant.CollectionName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = CollectionName.RESUME)
public class ResumeDocument {

    @Id
    private String id;
    
    private String profileId;

    private String originalFileName;
    

    /**
     * Reference to the physical resume file.
     * Initially this can be a local file path.
     */
    private String storageReference;

    /**
     * Text extracted from PDF/DOCX.
     */
    private String extractedText;

    private CandidateProfile profile;

    private LocalDateTime uploadedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateProfile {

        private String name;

        private String email;
        private String phoneNum;

        private String profileType;

        private String profileSummary;

        private Double yearsOfExperience;

        private List<String> skills;

        private List<String> domains;

        private List<String> pastCompanies;

        private String location;

        private String country;

        private String qualification;

        private String specialization;

        private String institute;

        private List<String> certifications;

        private String appliedPosition;

        private LocalDate appliedDate;
    }
}
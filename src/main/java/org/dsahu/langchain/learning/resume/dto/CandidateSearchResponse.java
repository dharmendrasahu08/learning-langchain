package org.dsahu.langchain.learning.resume.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSearchResponse {

    private String profileId;

    private String name;

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

    private Float score;
}
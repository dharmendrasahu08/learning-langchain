package org.dsahu.langchain.learning.resume.dto;

import java.util.List;

public record CandidateSearchRequest(
        String query,
        String country,
        String location,
        String profileType,
        Integer minExperience,
        Integer maxExperience,
        List<String> skills,
        int limit
) {
}
package org.dsahu.langchain.learning.resume.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record ResumeSearchRequest(
        String profileType,
        String country,
        String location,
        Double minExperience,
        List<String> skills,
        List<String> domains
) {
}
package org.dsahu.langchain.learning.resume.service;

import lombok.extern.slf4j.Slf4j;
import org.dsahu.langchain.learning.resume.entity.ResumeDocument;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CandidateSearchTextBuilder {

    public String build(ResumeDocument.CandidateProfile profile) {

        log.debug("build#Building searchable text for candidate: {}", profile.getName());

        String searchableText = """
                Candidate: %s
                Profile Type: %s
                Summary: %s
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
                """.formatted(
                profile.getName(),
                profile.getProfileType(),
                profile.getProfileSummary(),
                profile.getYearsOfExperience(),
                String.join(", ", profile.getSkills()),
                String.join(", ", profile.getDomains()),
                String.join(", ", profile.getPastCompanies()),
                profile.getLocation(),
                profile.getCountry(),
                profile.getQualification(),
                profile.getSpecialization(),
                profile.getInstitute(),
                String.join(", ", profile.getCertifications()),
                profile.getAppliedPosition()
        );

        log.debug("Searchable text built successfully for candidate: {}",
                profile.getName());

        return searchableText;
    }
}
package org.dsahu.langchain.learning.resume.service;

import lombok.RequiredArgsConstructor;
import org.dsahu.langchain.learning.resume.dto.ResumeCreateRequest;
import org.dsahu.langchain.learning.resume.dto.ResumeResponse;
import org.dsahu.langchain.learning.resume.entity.ResumeDocument;
import org.dsahu.langchain.learning.resume.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;

    @Override
    public ResumeResponse create(ResumeCreateRequest request) {

    	ResumeDocument.CandidateProfile profile =
    	        ResumeDocument.CandidateProfile.builder()
    	                .name(request.profile().name())
    	                .email(request.profile().email())
    	                .phoneNum(request.profile().phoneNum())
    	                .profileType(request.profile().profileType())
    	                .profileSummary(request.profile().profileSummary())
    	                .yearsOfExperience(request.profile().yearsOfExperience())
    	                .skills(request.profile().skills())
    	                .domains(request.profile().domains())
    	                .pastCompanies(request.profile().pastCompanies())
    	                .location(request.profile().location())
    	                .country(request.profile().country())
    	                .qualification(request.profile().qualification())
    	                .specialization(request.profile().specialization())
    	                .institute(request.profile().institute())
    	                .certifications(request.profile().certifications())
    	                .appliedPosition(request.profile().appliedPosition())
    	                .appliedDate(request.profile().appliedDate())
    	                .build();

        ResumeDocument document = ResumeDocument.builder()
                .originalFileName(request.originalFileName())
                .storageReference(request.storageReference())
                .extractedText(request.extractedText())
                .profile(profile)
                .uploadedAt(LocalDateTime.now())
                .build();

        ResumeDocument saved = resumeRepository.save(document);

        return toResponse(saved);
    }

    private ResumeResponse toResponse(ResumeDocument document) {

        ResumeDocument.CandidateProfile profile = document.getProfile();

        ResumeResponse.CandidateProfileResponse profileResponse =
                ResumeResponse.CandidateProfileResponse.builder()
                        .name(profile.getName())
                        .email(profile.getEmail())
                        .profileType(profile.getProfileType())
                        .profileSummary(profile.getProfileSummary())
                        .yearsOfExperience(profile.getYearsOfExperience())
                        .skills(profile.getSkills())
                        .domains(profile.getDomains())
                        .pastCompanies(profile.getPastCompanies())
                        .location(profile.getLocation())
                        .country(profile.getCountry())
                        .qualification(profile.getQualification())
                        .specialization(profile.getSpecialization())
                        .institute(profile.getInstitute())
                        .certifications(profile.getCertifications())
                        .appliedPosition(profile.getAppliedPosition())
                        .appliedDate(profile.getAppliedDate())
                        .build();

        return ResumeResponse.builder()
                .id(document.getId())
                .originalFileName(document.getOriginalFileName())
                .storageReference(document.getStorageReference())
                .extractedText(document.getExtractedText())
                .profile(profileResponse)
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}
package org.dsahu.langchain.learning.resume.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.dsahu.langchain.learning.common.exception.ResumeNotFoundException;
import org.dsahu.langchain.learning.resume.dto.ResumeCreateRequest;
import org.dsahu.langchain.learning.resume.dto.ResumeResponse;
import org.dsahu.langchain.learning.resume.dto.ResumeSearchRequest;
import org.dsahu.langchain.learning.resume.entity.ResumeDocument;
import org.dsahu.langchain.learning.resume.repository.ResumeRepository;
import org.dsahu.langchain.learning.resume.repository.ResumeSearchRepository;
import org.springframework.stereotype.Service;

import dev.langchain4j.data.embedding.Embedding;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeSearchRepository resumeSearchRepository;
    private final CandidateEmbeddingService candidateEmbeddingService;

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
                .profileId(UUID.randomUUID().toString())
                .profile(profile)
                .uploadedAt(LocalDateTime.now())
                .build();

        ResumeDocument saved = resumeRepository.save(document);

        return toResponse(saved);
    }
    
    @Override
    public List<ResumeResponse> findAll() {

        return resumeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Override
    public ResumeResponse findByProfileId(String profileId) {

        ResumeDocument document = resumeRepository.findByProfileId(profileId)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found with profileId: " + profileId));

        return toResponse(document);
    }
    
    @Override
    public List<ResumeResponse> search(ResumeSearchRequest request) {
        return resumeSearchRepository.search(request)
                .stream()
                .map(this::toResponse)
                .toList();
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
                .profileId(document.getProfileId())
                .originalFileName(document.getOriginalFileName())
                .storageReference(document.getStorageReference())
                .extractedText(document.getExtractedText())
                .profile(profileResponse)
                .uploadedAt(document.getUploadedAt())
                .build();
    }
    
    @Override
    public Embedding createEmbedding(String id) {

        ResumeDocument document = resumeRepository.findById(id)
                .orElseThrow(() ->
                        new ResumeNotFoundException("Resume not found with id: " + id));

        return candidateEmbeddingService.createEmbedding(document.getProfile());
    }
    
}
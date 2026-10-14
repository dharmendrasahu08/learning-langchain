package org.dsahu.langchain.learning.resume.controller;

import java.util.List;

import org.dsahu.langchain.learning.resume.dto.ResumeCreateRequest;
import org.dsahu.langchain.learning.resume.dto.ResumeResponse;
import org.dsahu.langchain.learning.resume.dto.ResumeSearchRequest;
import org.dsahu.langchain.learning.resume.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.langchain4j.data.embedding.Embedding;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<ResumeResponse> create(
            @Valid @RequestBody ResumeCreateRequest request) {

        ResumeResponse response = resumeService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ResumeResponse>> findAll() {

        return ResponseEntity.ok(resumeService.findAll());
    }
    
    @GetMapping("/{profileId}")
    public ResponseEntity<ResumeResponse> findById(
            @PathVariable String profileId) {

        return ResponseEntity.ok(resumeService.findByProfileId(profileId));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ResumeResponse>> search(
            @ModelAttribute ResumeSearchRequest request) {

        return ResponseEntity.ok(resumeService.search(request));
    }
    /*Do not expose ouutside of world, itis only learning and debuging*/
    @GetMapping("/{id}/embedding/dimension")
    public ResponseEntity<Integer> embeddingDimension(@PathVariable String id) {
        Embedding embedding = resumeService.createEmbedding(id);
        return ResponseEntity.ok(embedding.dimension());
    }
    /*Do not expose ouutside of world, itis only learning and debuging*/
    @GetMapping("/{id}/embedding")
    public ResponseEntity<List<Float>> embedding(@PathVariable String id) {
        Embedding embedding = resumeService.createEmbedding(id);
        return ResponseEntity.ok(embedding.vectorAsList());
    }
}
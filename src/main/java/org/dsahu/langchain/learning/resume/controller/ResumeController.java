package org.dsahu.langchain.learning.resume.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dsahu.langchain.learning.resume.dto.ResumeCreateRequest;
import org.dsahu.langchain.learning.resume.dto.ResumeResponse;
import org.dsahu.langchain.learning.resume.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
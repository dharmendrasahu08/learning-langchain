package org.dsahu.langchain.learning.resume.service;

import org.dsahu.langchain.learning.resume.dto.ResumeCreateRequest;
import org.dsahu.langchain.learning.resume.dto.ResumeResponse;

public interface ResumeService {

    ResumeResponse create(ResumeCreateRequest request);
}
package org.dsahu.langchain.learning.resume.dto;

import java.util.List;

public record EmbeddingResponse(
        int dimension,
        List<Float> vector
) {
}
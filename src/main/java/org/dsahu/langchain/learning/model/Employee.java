package org.dsahu.langchain.learning.model;

import java.util.List;

public record Employee(
        String name,
        Integer age,
        String role,
        Integer experience,
        String company,
        String city,
        List<String> skills
) {
}
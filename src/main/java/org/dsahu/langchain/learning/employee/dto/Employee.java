package org.dsahu.langchain.learning.employee.dto;

import java.util.List;

public record Employee(
        String employeeId,
        String name,
        Integer age,
        String role,
        Integer experience,
        String company,
        String city,
        List<String> skills
) {
}
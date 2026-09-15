package org.dsahu.langchain.learning.entity.business;

import org.dsahu.langchain.learning.constant.CollectionName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = CollectionName.EMPLOYEES)
public class EmployeeDocument {

    @Id
    private String employeeId;

    private String name;
    private Integer age;
    private String role;
    private Integer experience;
    private String company;
    private String city;
    private List<String> skills;

    public EmployeeDocument() {
    }

    public EmployeeDocument(
            String employeeId,
            String name,
            Integer age,
            String role,
            Integer experience,
            String company,
            String city,
            List<String> skills) {

        this.employeeId = employeeId;
        this.name = name;
        this.age = age;
        this.role = role;
        this.experience = experience;
        this.company = company;
        this.city = city;
        this.skills = skills;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}

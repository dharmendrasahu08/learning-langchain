package org.dsahu.langchain.learning.user.entity;

import org.dsahu.langchain.learning.common.constant.CollectionName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = CollectionName.USERS)
public class UserDocument {

    @Id
    private String userId;

    private String username;

    private String password;

    private String role;

    private String employeeId;

    public UserDocument() {
    }

    public UserDocument(
            String userId,
            String username,
            String password,
            String role,
            String employeeId) {

        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}

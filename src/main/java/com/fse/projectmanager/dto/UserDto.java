package com.fse.projectmanager.dto;

import lombok.Builder;

@Builder
public class UserDto {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String employeeId;

    public UserDto(Integer userId, String firstName, String lastName, String employeeId) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeId = employeeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmployeeId() {
        return employeeId;
    }
}

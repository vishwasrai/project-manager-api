package com.fse.projectmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDto {
    private Integer projectId;
    private String project;
    private String startDate;
    private String endDate;
    private Boolean setStartAndEndDate;
    private Integer priority;
    private UserDto manager;
    private Integer managerId;
    private Boolean completed;
    private Integer noOfTasks;
    private List<TaskDto> tasks;
    private Integer noOfCompletedTasks;

    public ProjectDto() {

    }

    public ProjectDto(Integer projectId) {
        this.projectId = projectId;
    }

    public ProjectDto(Integer projectId, String project, String startDate, String endDate, Boolean setStartAndEndDate, Integer priority, Integer managerId) {
        this.projectId = projectId;
        this.project = project;
        this.startDate = startDate;
        this.endDate = endDate;
        this.setStartAndEndDate = setStartAndEndDate;
        this.priority = priority;
        this.managerId = managerId;
    }
}

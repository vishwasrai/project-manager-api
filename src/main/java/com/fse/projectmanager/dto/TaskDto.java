package com.fse.projectmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {
    private Integer taskId;
    private Integer projectId;
    private String projectName;
    private String task;
    private Boolean thisIsParent;
    private Integer priority;
    private String startDate;
    private String endDate;
    private Integer userId;
    private String userFirstName;
    private String userLastName;
    private String status;
    private Integer parentTaskId;
    private String parentTaskName;

    public TaskDto() {

    }

    public TaskDto(Integer taskId, String task) {
        this.taskId = taskId;
        this.task = task;
    }

    public TaskDto(Integer taskId, Integer projectId, String task, Boolean thisIsParent, Integer priority, Integer parentTaskId, String startDate, String endDate, Integer userId, String status) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.task = task;
        this.thisIsParent = thisIsParent;
        this.priority = priority;
        this.parentTaskId = parentTaskId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId;
        this.status = status;
    }
}

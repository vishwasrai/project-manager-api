package com.fse.projectmanager.service;

import com.fse.projectmanager.dto.TaskDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectManagerService {

    TaskDto saveTask(TaskDto taskDto);

    List<TaskDto> getAllTasks();

    TaskDto getTaskById(Integer taskId);

}

package com.fse.projectmanager.service;

import com.fse.projectmanager.dto.TaskDto;
import com.fse.projectmanager.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectManagerService {
    UserDto saveUser(UserDto userDto);

    List<UserDto> getAllUsers();

    Boolean deleteUser(Integer userId);

    TaskDto saveTask(TaskDto taskDto);

    List<TaskDto> getAllTasks();

    List<UserDto> getAvailableManagers();

    List<UserDto> getAvailableUsersForTask();

    TaskDto getTaskById(Integer taskId);

}

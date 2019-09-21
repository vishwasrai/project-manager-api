package com.fse.projectmanager.controller;

import com.fse.projectmanager.dto.ResponseDto;
import com.fse.projectmanager.dto.TaskDto;
import com.fse.projectmanager.dto.UserDto;
import com.fse.projectmanager.service.ProjectManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class ProjectManagerController {
    private static final String FAILURE = "failure";
    private static final String SUCCESS = "success";

    Logger logger = LoggerFactory.getLogger(ProjectManagerController.class);

    @Autowired
    private ProjectManagerService projectManagerService;

    @PostMapping("/saveUser")
    public ResponseDto saveUser(@RequestBody UserDto userDto) {
        ResponseDto response = createFailureResponse();
        try {
            userDto = projectManagerService.saveUser(userDto);
            response = createSuccessResponse(userDto);
        } catch (Exception e) {
            logger.error("There was error while saving user: " + userDto.getFirstName() + " Cause: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/getAllUsers")
    public ResponseDto getAllUsers() {
        try {
            return createSuccessResponse(projectManagerService.getAllUsers());
        } catch (Exception e) {
            logger.error("There was error while retriving all users. Cause: " + e.getMessage());
            return createFailureResponse();
        }
    }

    @GetMapping("/getAvailableManagers")
    public ResponseDto getAvailableManagers() {
        try {
            return createSuccessResponse(projectManagerService.getAvailableManagers());
        } catch (Exception e) {
            logger.error("There was error while retriving all managers. Cause: " + e.getMessage());
            return createFailureResponse();
        }
    }

    @GetMapping("/getAvailableUsersForTask")
    public ResponseDto getAvailableUsersForTask() {
        try {
            return createSuccessResponse(projectManagerService.getAvailableUsersForTask());
        } catch (Exception e) {
            logger.error("There was error while retriving all users for task. Cause: " + e.getMessage());
            return createFailureResponse();
        }
    }

    @DeleteMapping("/deleteUserById/{userId}")
    public ResponseDto deleteUserById(@PathVariable("userId") Integer userId) {
        ResponseDto response = createFailureResponse();
        try {
            response = createSuccessResponse(projectManagerService.deleteUser(userId));
        } catch (Exception e) {
            logger.error("There was error while deleting user: " + userId + " Cause: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/saveTask")
    public ResponseDto saveTask(@RequestBody TaskDto taskDto) {
        ResponseDto response = createFailureResponse();
        try {
            taskDto = projectManagerService.saveTask(taskDto);
            response = createSuccessResponse(taskDto);
        } catch (Exception e) {
            logger.error("There was error while saving task: " + taskDto.getTask() + " Cause: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/getAllTasks")
    public ResponseDto getAllTasks() {
        try {
            return createSuccessResponse(projectManagerService.getAllTasks());
        } catch (Exception e) {
            logger.error("There was error while retrieving all tasks. Cause: " + e.getMessage());
            return createFailureResponse();
        }
    }

    @GetMapping("/getTaskById/{taskId}")
    public ResponseDto getTaskById(@PathVariable("taskId") Integer taskId) {
        ResponseDto response = createFailureResponse();
        try {
            response = createSuccessResponse(projectManagerService.getTaskById(taskId));
        } catch (Exception e) {
            logger.error("There was error while loading task: " + taskId + " Cause: " + e.getMessage());
        }
        return response;
    }

    private ResponseDto createSuccessResponse(Object data) {
        ResponseDto response = new ResponseDto();
        response.setResult(SUCCESS);
        if (data instanceof List<?>) {
            response.setObjectList(data);
        } else {
            response.setObject(data);
        }
        return response;
    }

    private ResponseDto createFailureResponse() {
        ResponseDto response = new ResponseDto();
        response.setResult(FAILURE);
        return response;
    }
}

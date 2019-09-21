package com.fse.projectmanager.service;

import com.fse.projectmanager.dto.TaskDto;
import com.fse.projectmanager.dto.UserDto;
import com.fse.projectmanager.entity.ParentTask;
import com.fse.projectmanager.entity.Task;
import com.fse.projectmanager.entity.User;
import com.fse.projectmanager.repository.ParentTaskRepository;
import com.fse.projectmanager.repository.TaskRepository;
import com.fse.projectmanager.repository.UserRepository;
import com.fse.projectmanager.utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProjectManagerServiceImpl implements ProjectManagerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ParentTaskRepository parentTaskRepository;

    @Override
    public UserDto saveUser(UserDto userDto) {
        try {
            User user = userRepository.save(userDtoToEntity(userDto));
            userDto.setUserId(user.getUserId());
        } catch (Exception e) {
            throw e;
        }
        return userDto;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtos = new ArrayList<>();

        List<User> users = (List<User>) userRepository.findAll();
        for (User user : users) {
            userDtos.add(userEntityToDto(user));
        }

        return userDtos;
    }

    @Override
    public List<UserDto> getAvailableManagers() {
        List<UserDto> userDtos = new ArrayList<>();

        return userDtos;
    }

    @Override
    public List<UserDto> getAvailableUsersForTask() {
        List<UserDto> userDtos = new ArrayList<>();
        List<User> users = userRepository.findAllByTaskIsNull();
        for (User user : users) {
            userDtos.add(userEntityToDto(user));
        }
        return userDtos;
    }

    @Override
    public Boolean deleteUser(Integer userId) {
        try {
            if (userId == null || userId <= 0) {
                throw new RuntimeException("Invalid UserID passed");
            }
            userRepository.deleteById(userId);
        } catch (Exception e) {
            throw e;
        }
        return true;
    }

    @Override
    public TaskDto saveTask(TaskDto taskDto) {
        try {
            ParentTask parentTask = parentTaskDtoToEntity(taskDto);
            if (parentTask != null)
                parentTaskRepository.save(parentTask);
            Task task = taskDtoToEntity(taskDto);
            task = taskRepository.save(task);
            User user = userRepository.findByTask(task);
            if (user != null) {
                user.setTask(null);
                user = userRepository.save(user);
            }
            if (taskDto.getUserId() != null) {
                user = userRepository.findById(taskDto.getUserId()).get();
            }
            if (user != null) {
                user.setTask(task);
                userRepository.save(user);
            }
            taskDto.setTaskId(task.getTaskId());
        } catch (Exception e) {
            throw e;
        }
        return taskDto;
    }

    @Override
    public List<TaskDto> getAllTasks() {
        List<TaskDto> taskDtos = new ArrayList<>();
        try {
            List<Task> tasks = (List<Task>) taskRepository.findAll();
            for (Task task : tasks) {
                taskDtos.add(taskEntityToDto(task));
            }
        } catch (Exception e) {
            throw e;
        }
        return taskDtos;
    }

    @Override
    public TaskDto getTaskById(Integer taskId) {
        TaskDto taskDto;
        try {
            taskDto = taskEntityToDto(taskRepository.findById(taskId).get());
        } catch (Exception e) {
            throw e;
        }
        return taskDto;
    }

    private User userDtoToEntity(UserDto userDto) {


        User user = new User();

        user.setUserId(userDto.getUserId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmployeeId(userDto.getEmployeeId());
        return user;
    }

    private UserDto userEntityToDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmployeeId(user.getEmployeeId());
        return userDto;
    }

    private TaskDto taskEntityToDto(Task task) {
        TaskDto taskDto = new TaskDto();

        taskDto.setTaskId(task.getTaskId());
        taskDto.setTask(task.getTask());
        taskDto.setEndDate(DateConverter.convert(task.getEndDate()));
        if (task.getParent() != null) {
            taskDto.setParentTaskId(task.getParent().getTaskId());
            taskDto.setParentTaskName(task.getParent().getTask());
        }
        taskDto.setPriority(task.getPriority());
        taskDto.setStartDate(DateConverter.convert(task.getStartDate()));
        taskDto.setStatus(task.getStatus());
        User user = userRepository.findByTask(task);
        if (user != null) {
            taskDto.setUserId(user.getUserId());
            taskDto.setUserFirstName(user.getFirstName());
            taskDto.setUserLastName(user.getLastName());
        }

        ParentTask parentTask = parentTaskRepository.findFirstByParentTask(task.getTask());
        if (parentTask != null) {
            taskDto.setThisIsParent(true);
        } else {
            taskDto.setThisIsParent(false);
        }

        return taskDto;
    }

    private Task taskDtoToEntity(TaskDto taskDto) {
        Task task = new Task();

        task.setTaskId(taskDto.getTaskId());
        task.setTask(taskDto.getTask());
        task.setEndDate(DateConverter.convert(taskDto.getEndDate()));
        if (taskDto.getParentTaskId() != null) {
            task.setParent(taskRepository.findById(taskDto.getParentTaskId()).get());
        }
        task.setPriority(taskDto.getPriority());
        task.setStartDate(DateConverter.convert(taskDto.getStartDate()));
        task.setStatus(taskDto.getStatus());

        return task;
    }

    private ParentTask parentTaskDtoToEntity(TaskDto taskDto) {
        ParentTask parentTask;

        if (taskDto.getThisIsParent() != null && taskDto.getThisIsParent()) {
            parentTask = new ParentTask();
            parentTask.setParentTask(taskDto.getTask());
        } else {
            parentTask = parentTaskRepository.findFirstByParentTask(taskDto.getTask());
        }
        return parentTask;
    }
}

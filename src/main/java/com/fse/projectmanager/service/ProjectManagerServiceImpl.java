package com.fse.projectmanager.service;

import com.fse.projectmanager.dto.ProjectDto;
import com.fse.projectmanager.dto.TaskDto;
import com.fse.projectmanager.dto.UserDto;
import com.fse.projectmanager.entity.ParentTask;
import com.fse.projectmanager.entity.Project;
import com.fse.projectmanager.entity.Task;
import com.fse.projectmanager.entity.User;
import com.fse.projectmanager.repository.ParentTaskRepository;
import com.fse.projectmanager.repository.ProjectRepository;
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
    private ProjectRepository projectRepository;

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

        List<User> users = userRepository.findAllByProjectIsNull();
        for (User user : users) {
            userDtos.add(userEntityToDto(user));
        }

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
    public ProjectDto saveProject(ProjectDto projectDto) {
        try {
            Project project = projectDtoToEntity(projectDto);
            project = projectRepository.save(project);
            projectDto.setProjectId(project.getProjectId());
            User user = userRepository.findByProject(project);
            if (user != null) {
                user.setProject(null);
                userRepository.save(user);
            }
            user = userRepository.findById(projectDto.getManagerId()).orElse(null);
            if (user != null) {
                user.setProject(project);
                userRepository.save(user);
            }
        } catch (Exception e) {
            throw e;
        }
        return projectDto;
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        List<ProjectDto> projectDtos = new ArrayList<>();
        try {
            List<Project> projects = (List<Project>) projectRepository.findAll();
            for (Project project : projects) {
                projectDtos.add(projectEntityToDto(project));
            }
        } catch (Exception e) {
            throw e;
        }
        return projectDtos;
    }

    @Override
    public Boolean deleteProject(Integer projectId) {
        try {
            if (projectId == null || projectId <= 0) {
                throw new RuntimeException("Invalid UserID passed");
            }
            Project project = projectRepository.findById(projectId).get();
            User user = userRepository.findByProject(project);
            if (user != null) {
                user.setProject(null);
                userRepository.save(user);
            }
            projectRepository.delete(project);
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

        return User.builder()
                .userId(userDto.getUserId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .employeeId(userDto.getEmployeeId())
                .build();
    }

    private UserDto userEntityToDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .employeeId(user.getEmployeeId())
                .build();
    }

    private ProjectDto projectEntityToDto(Project project) {
        ProjectDto projectDto = new ProjectDto();

        projectDto.setProjectId(project.getProjectId());
        projectDto.setProject(project.getProject());
        projectDto.setPriority(project.getPriority());
        projectDto.setStartDate(DateConverter.convert(project.getStartDate()));
        projectDto.setEndDate(DateConverter.convert(project.getEndDate()));

        projectDto.setManager(userEntityToDto(userRepository.findByProject(project)));
        projectDto.setManagerId(projectDto.getManager() != null ? projectDto.getManager().getUserId() : null);

        projectDto.setCompleted(project.getEndDate() != null ? (project.getEndDate().compareTo(new Date()) == -1) : false);

        int completedTasks = 0;
        List<TaskDto> taskDtos = new ArrayList<>();
        for (Task task : project.getTasks()) {
            taskDtos.add(taskEntityToDto(task));
            if (task.getStatus() != null && task.getStatus().equalsIgnoreCase("completed")) {
                completedTasks = completedTasks + 1;
            }
        }
        projectDto.setTasks(taskDtos);
        projectDto.setNoOfCompletedTasks(completedTasks);
        projectDto.setNoOfTasks(project.getTasks().size());
        projectDto.setSetStartAndEndDate(project.getEndDate() != null ? (project.getEndDate().compareTo(new Date()) == 0) : false);

        return projectDto;
    }

    private Project projectDtoToEntity(ProjectDto projectDto) {
        return Project.builder()
                .projectId(projectDto.getProjectId())
                .project(projectDto.getProject())
                .priority(projectDto.getPriority())
                .startDate(DateConverter.convert(projectDto.getStartDate()))
                .endDate(DateConverter.convert(projectDto.getEndDate()))
                .build();
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
        if (task.getProject() != null) {
            taskDto.setProjectId(task.getProject().getProjectId());
            taskDto.setProjectName(task.getProject().getProject());
        }
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
        task.setProject(projectRepository.findById(taskDto.getProjectId()).get());
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

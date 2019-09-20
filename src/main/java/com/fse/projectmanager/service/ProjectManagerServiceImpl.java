package com.fse.projectmanager.service;

import com.fse.projectmanager.dto.TaskDto;
import com.fse.projectmanager.entity.ParentTask;
import com.fse.projectmanager.entity.Task;
import com.fse.projectmanager.repository.ParentTaskRepository;
import com.fse.projectmanager.repository.TaskRepository;
import com.fse.projectmanager.utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProjectManagerServiceImpl implements ProjectManagerService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ParentTaskRepository parentTaskRepository;


    @Override
    public TaskDto saveTask(TaskDto taskDto) {
        try {
            ParentTask parentTask = parentTaskDtoToEntity(taskDto);
            if (parentTask != null)
                parentTaskRepository.save(parentTask);
            Task task = taskDtoToEntity(taskDto);
            task = taskRepository.save(task);
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

package com.fse.projectmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fse.projectmanager.ProjectManagerApiApplication;
import com.fse.projectmanager.dto.TaskDto;
import com.fse.projectmanager.dto.UserDto;
import com.fse.projectmanager.entity.ParentTask;
import com.fse.projectmanager.entity.Task;
import com.fse.projectmanager.entity.User;
import com.fse.projectmanager.repository.ParentTaskRepository;
import com.fse.projectmanager.repository.TaskRepository;
import com.fse.projectmanager.repository.UserRepository;
import com.fse.projectmanager.service.ProjectManagerService;
import com.fse.projectmanager.utils.DateConverter;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

import static org.hamcrest.Matchers.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ProjectManagerApiApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectManagerControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProjectManagerService projectManagerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParentTaskRepository parentTaskRepository;
    @Autowired
    private TaskRepository taskRepository;

    User userToDelete;
    User manager;
    ParentTask parentTask;
    Task taskToDelete;
    Task taskWithParent;
    Task task;
    Task taskToUpdate;


    @Before
    public void setup() {
        userToDelete = userRepository.save(getMockUser(null, "Blah", "Test", "10", null));
        manager = userRepository.save(getMockUser(null, "Blah Blah", "Manager", "11", null));

        userRepository.save(getMockUser(null, "Test", "Test", "13", null));
        parentTask = parentTaskRepository.save(getMockParentTask(null, "Default Task"));
        task = taskRepository.save(getMockTask(null, "Default Task", DateConverter.convert("2019-01-01"), null, 1, "completed", null));
        taskToUpdate = taskRepository.save(getMockTask(null, "Task with same end date", DateConverter.convert("2019-01-01"), DateConverter.convert("2019-01-01"), 1, null,  null));
        taskRepository.save(getMockTask(null, "Task with greater end date", DateConverter.convert("2019-01-01"), DateConverter.convert("2019-02-01"), 1, null,  null));
        taskRepository.save(getMockTask(null, "Task with future end date", DateConverter.convert("2019-01-01"), DateConverter.convert("2025-02-01"), 1, null, null));
        task = taskRepository.findById(task.getTaskId()).get();
        taskWithParent = taskRepository.save(getMockTask(null, "Default Task One", DateConverter.convert("2019-01-01"), null, 1, null,  task));
        taskToDelete = taskRepository.save(getMockTask(null, "Default Task Two", DateConverter.convert("2019-01-01"), null, 1, null,  null));
        // With Task
        userRepository.save(getMockUser(null, "Test Task", "Test", "14", taskToUpdate));
    }

    @Test
    public void testASaveUser() throws Exception {
        UserDto userDto = new UserDto(null, "Blah", "Test", "10");
        mvc.perform(MockMvcRequestBuilders
                .post("/saveUser")
                .content(asJsonString(userDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId", greaterThan(0)));

    }

    @Test
    public void testASaveUserError() throws Exception {
        UserDto userDto = new UserDto(null, null, null, null);
        mvc.perform(MockMvcRequestBuilders
                .post("/saveUser")
                .content(asJsonString(userDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", is("failure")));
    }

    @Test
    public void testAGetAllUsers() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/getAllUsers")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", is("success")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataList", hasSize(greaterThan(0))));
    }

    @Test
    public void testAGetAvailableUsersForTask() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/getAvailableUsersForTask")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", is("success")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataList", hasSize(greaterThan(0))));
    }

    @Test
    public void testADeleteUserById() throws Exception {
        Integer userId = userToDelete.getUserId();
        mvc.perform(MockMvcRequestBuilders.delete("/deleteUserById/" + userId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", is("success")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", is(true)));
    }

    @Test
    public void testADeleteUserByIdFailure() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/deleteUserById/1000")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", is("failure")));
    }

    @Test
    public void testADeleteUserByIdFailureForZero() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/deleteUserById/0")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", is("failure")));
    }

    @Test
    public void testCSaveTask() throws Exception {
        TaskDto taskDto = new TaskDto(null, "Test Task", false, 1, null, "2019-05-30", null, manager.getUserId(), null);
        mvc.perform(MockMvcRequestBuilders
                .post("/saveTask")
                .content(asJsonString(taskDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.taskId", greaterThan(0)));

    }
    @Test
    public void testCUpdateTask() throws Exception {
        TaskDto taskDto = new TaskDto(taskToUpdate.getTaskId(), "Test Task", false, 1, null, "2019-05-30", null, manager.getUserId(), null);
        mvc.perform(MockMvcRequestBuilders
                .post("/saveTask")
                .content(asJsonString(taskDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.taskId", greaterThan(0)));

    }
    @Test
    public void testCSaveTaskWIthParent() throws Exception {
        TaskDto taskDto = new TaskDto(null, "Test Task", false, 1, task.getTaskId(), "2019-05-30", null, manager.getUserId(), null);
        mvc.perform(MockMvcRequestBuilders
                .post("/saveTask")
                .content(asJsonString(taskDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.taskId", greaterThan(0)));

    }
    @Test
    public void testCSaveTaskWIthParentBlak() throws Exception {
        TaskDto taskDto = new TaskDto(null, "Test Task", false, 1, null, "2019-05-30", null, manager.getUserId(), null);
        mvc.perform(MockMvcRequestBuilders
                .post("/saveTask")
                .content(asJsonString(taskDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.taskId", greaterThan(0)));

    }
    @Test
    public void testCSaveTaskThisIsParent() throws Exception {
        TaskDto taskDto = new TaskDto(null, "Test Task", true, 1, null, "2019-05-30", null, manager.getUserId(), null);
        mvc.perform(MockMvcRequestBuilders
                .post("/saveTask")
                .content(asJsonString(taskDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.taskId", greaterThan(0)));

    }

    @Test
    public void testCSaveTaskFailure() throws Exception {
        TaskDto taskDto = new TaskDto();

        mvc.perform(MockMvcRequestBuilders
                .post("/saveTask")
                .content(asJsonString(taskDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", is("failure")));
    }

    @Test
    public void testCGetAllTasks() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/getAllTasks")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", is("success")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataList", hasSize(greaterThan(0))));
    }
    @Test
    public void testCGetTaskByTaskId() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/getTaskById/" + task.getTaskId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", is("success")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.taskId", is(task.getTaskId())));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User getMockUser(Integer userId, String firstName, String lastName, String employeeID, Task task) {
        User user = new User();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmployeeId(employeeID);
        user.setTask(task);
        return user;
    }

    private Task getMockTask(Integer taskId, String taskName, Date startDate,
                             Date endDate, Integer priority, String status,
                             Task parentTask) {
        Task task = new Task();

        task.setTaskId(taskId);
        task.setTask(taskName);
        task.setEndDate(endDate);
        task.setParent(parentTask);
        task.setPriority(priority);
        task.setStartDate(startDate);
        task.setStatus(status);

        return task;
    }

    private ParentTask getMockParentTask(Integer parentTaskId, String parentTaskName) {
        ParentTask parentTask = new ParentTask();
        parentTask.setParentId(parentTaskId);
        parentTask.setParentTask(parentTaskName);
        return parentTask;
    }
}

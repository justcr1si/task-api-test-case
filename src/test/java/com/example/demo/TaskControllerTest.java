package com.example.demo;

import com.example.demo.models.TaskPriority;
import com.example.demo.models.TaskStatus;
import com.example.demo.schema.TaskRequest;
import com.example.demo.schema.TaskResponse;
import com.example.demo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTask_Success() throws Exception {
        TaskRequest request = new TaskRequest("Task title", "Description", TaskStatus.IN_PROGRESS, TaskPriority.HIGH);
        TaskResponse response = TaskResponse.builder()
                .id(1L)
                .title("Task title")
                .description("Description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .assigneeUsername("justcr1si")
                .authorUsername("justcr1si")
                .build();

        when(taskService.createTask(any(TaskRequest.class), anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/tasks")
                        .param("authorId", "1")
                        .param("assigneeId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createTask_ForbiddenForUser() throws Exception {
        TaskRequest request = new TaskRequest("Task title", "Description", TaskStatus.IN_PROGRESS, TaskPriority.HIGH);

        mockMvc.perform(post("/tasks")
                        .param("authorId", "1")
                        .param("assigneeId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}

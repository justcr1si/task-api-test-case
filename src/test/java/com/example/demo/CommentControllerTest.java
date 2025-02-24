package com.example.demo;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.schema.CommentRequest;
import com.example.demo.schema.CommentResponse;
import com.example.demo.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"}, username = "test123@gmail.com")
    void createComment_Success() throws Exception {
        when(userRepository.findByEmail("test123@gmail.com"))
                .thenReturn(Optional.of(User.builder()
                        .id(1L)
                        .email("test123@gmail.com")
                        .username("test")
                        .password("test")
                        .role(Role.USER)
                        .build()));

        CommentRequest commentRequest = new CommentRequest("Text");
        CommentResponse commentResponse = CommentResponse.builder()
                .id(1L)
                .text(commentRequest.getText())
                .authorUsername("test123@gmail.com")
                .taskTitle("Some task")
                .build();

        when(commentService.addComment(any(CommentRequest.class), anyLong(), anyLong())).thenReturn(commentResponse);

        mockMvc.perform(post("/comments/leave-comment/")
                        .param("taskId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void createComment_Unauthorized() throws Exception {
        CommentRequest request = new CommentRequest("Комментарий без авторизации");

        mockMvc.perform(post("/comments/leave-comment")
                        .param("taskId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createComment_TaskNotFound() throws Exception {
        CommentRequest request = new CommentRequest("Комментарий к несуществующей задаче");

        when(commentService.addComment(any(CommentRequest.class), anyLong(), anyLong()))
                .thenThrow(new EntityNotFoundException("Task not found"));

        mockMvc.perform(post("/comments/leave-comment")
                        .param("taskId", "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

}

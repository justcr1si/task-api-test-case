package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.controller.CommentController;
import com.example.demo.models.Comment;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.schema.CommentRequest;
import com.example.demo.schema.CommentResponse;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentController commentController;

    private final String username = "test123@gmail.com";
    private final String password = "test123";
    private final String role = "ADMIN";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .build();
    }

    @Test
    void getCommentsByTaskId_Success() throws Exception {
        Long taskId = 1L;
        List<CommentResponse> comments = List.of(new CommentResponse(1L, "Great task!", "user", "Task 1", LocalDateTime.now()));

        lenient().when(commentService.getCommentsByTaskId(taskId)).thenReturn(comments);

        mockMvc.perform(get("/comments/task")
                        .param("taskId", taskId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllComments_Success() throws Exception {
        List<CommentResponse> comments = List.of(new CommentResponse(1L, "Great task!", "user", "Task 1", LocalDateTime.now()));
        lenient().when(commentService.getAllComments()).thenReturn(comments);

        mockMvc.perform(get("/comments"))
                .andExpect(status().isOk());
    }

    @Test
    void getCommentsWithPagination_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        Task task = Task.builder().id(1L).title("Task 1").build();
        User author = User.builder().id(1L).username("user").build();

        List<Comment> comments = List.of(Comment.builder()
                .id(1L)
                .text("Some text")
                .task(task)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build()
        );

        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        lenient().when(commentService.getComments(0, 10)).thenReturn(commentPage.map(
                page -> new CommentResponse(
                        1L,
                        "Some text",
                        "Some title",
                        "Example",
                        LocalDateTime.now()
                )
        ));

        mockMvc.perform(get("/comments/pagination")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void leaveComment_Success() throws Exception {
        CommentRequest commentRequest = new CommentRequest("Great task!");
        CommentResponse commentResponse = new CommentResponse(1L, "Great task!", "user", "Task 1", LocalDateTime.now());

        Task task = Task
                .builder()
                .id(1L)
                .title("test")
                .description("test")
                .build();

        Long taskId = 1L;

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@gmail.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(commentService.addComment(any(CommentRequest.class), anyLong(), anyLong())).thenReturn(commentResponse);

        mockMvc.perform(post("/comments")
                        .param("taskId", taskId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentRequest)))
                .andExpect(status().isCreated());
    }
}


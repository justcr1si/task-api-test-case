package com.example.demo;

import com.example.demo.controller.CommentController;
import com.example.demo.models.Comment;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.schema.CommentRequest;
import com.example.demo.schema.CommentResponse;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentController commentController;

    private final String username = "test123@gmail.com";
    private final String password = "test123";
    private final String role = "ADMIN";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    private List<CommentResponse> createMockComments() {
        return List.of(new CommentResponse(1L, "Great task!", "user", "Task 1", LocalDateTime.now()));
    }

    @Test
    @WithMockUser(username = username, password = password, roles = role)
    void getCommentsByTaskId_Success() throws Exception {
        Long taskId = 1L;
        List<CommentResponse> comments = createMockComments();
        lenient().when(commentService.getCommentsByTaskId(taskId)).thenReturn(comments);

        mockMvc.perform(get("/comments/task")
                        .param("taskId", taskId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = username, password = password, roles = role)
    void getAllComments_Success() throws Exception {
        List<CommentResponse> comments = createMockComments();
        lenient().when(commentService.getAllComments()).thenReturn(comments);

        mockMvc.perform(get("/comments"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = username, password = password, roles = role)
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
                comment -> new CommentResponse(
                        comment.getId(),
                        comment.getText(),
                        comment.getTask().getTitle(),
                        comment.getAuthor().getUsername(),
                        comment.getCreatedAt()
                )
        ));

        mockMvc.perform(get("/comments/pagination")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = username, password = password, roles = role)
    void leaveComment_Success() throws Exception {
        CommentRequest commentRequest = new CommentRequest("Great task!");
        Long taskId = 1L;

        User mockUser = User.builder().id(1L).username(username).build();
        Optional<User> optionalUser = Optional.of(mockUser);

        when(userRepository.findByEmail(username)).thenReturn(optionalUser);

        mockMvc.perform(post("/comments/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentRequest)))
                .andExpect(status().isCreated());
    }
}


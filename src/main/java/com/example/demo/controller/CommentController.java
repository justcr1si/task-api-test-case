package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.schema.CommentRequest;
import com.example.demo.schema.CommentResponse;
import com.example.demo.schema.TaskResponse;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Контроллер авторизации")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping("/task")
    @Operation(summary = "Получение комментария по ID")
    public ResponseEntity<List<CommentResponse>> getCommentsByTaskId(@RequestParam Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId));
    }

    @GetMapping
    @Operation(summary = "Получение всех комментариев")
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/pagination")
    @Operation(summary = "Получение комментариев и пагинация")
    public ResponseEntity<Page<CommentResponse>> getComments(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getComments(page, size));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    @Operation(summary = "Оставление комментария к таске по taskId и CommentRequest")
    public ResponseEntity<TaskResponse> leaveComment(@RequestParam Long taskId, @RequestBody CommentRequest commentRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);
        commentService.addComment(commentRequest, currentUser.getId(), taskId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    @Operation(summary = "Удаление комментария по ID")
    public ResponseEntity<Void> deleteComment(@RequestParam Long commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        }

        CommentResponse commentResponse = commentService.getCommentById(commentId);
        if (!commentResponse.getAuthorUsername().equals(currentUser.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Operation(summary = "Обновление комментария по ID")
    public ResponseEntity<CommentResponse> updateComment(@RequestParam Long commentId, @RequestBody CommentRequest commentRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);
        CommentResponse commentResponse;
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            commentResponse = commentService.getCommentById(commentId);
            if (commentResponse.getAuthorUsername().equals(currentUser.getUsername())) {
                commentService.updateComment(commentId, commentRequest);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(commentService.updateComment(commentId, commentRequest));
    }
}

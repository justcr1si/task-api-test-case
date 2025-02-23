package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.schema.CommentRequest;
import com.example.demo.schema.CommentResponse;
import com.example.demo.schema.TaskResponse;
import com.example.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{taskId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/leave-comment/{taskId}")
    public ResponseEntity<TaskResponse> leaveComment(@PathVariable Long taskId, @RequestBody CommentRequest commentRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        commentService.addComment(commentRequest, user.getId(), taskId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok(commentService.updateComment(commentId, commentRequest));
    }
}

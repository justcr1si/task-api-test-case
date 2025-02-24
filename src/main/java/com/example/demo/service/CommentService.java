package com.example.demo.service;

import com.example.demo.exceptions.CommentNotFoundException;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.Comment;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.schema.CommentRequest;
import com.example.demo.schema.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public CommentResponse addComment(CommentRequest commentRequest, Long authorId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .text(commentRequest.getText())
                .task(task)
                .author(author)
                .build();

        commentRepository.save(comment);

        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorUsername(author.getUsername())
                .taskTitle(task.getTitle())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public List<CommentResponse> getAllComments() {
        return commentRepository.findAllProjectedComments().stream()
                .map(comment -> CommentResponse.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .taskTitle(comment.getTaskTitle())
                        .authorUsername(comment.getAuthorUsername())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public CommentResponse updateComment(Long commentId, CommentRequest commentRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        comment.setText(commentRequest.getText());
        commentRepository.save(comment);
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorUsername(comment.getAuthor().getUsername())
                .taskTitle(comment.getTask().getTitle())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public CommentResponse getCommentById(Long commentId) {
        return commentRepository.findCommentsById(commentId);
    }
}

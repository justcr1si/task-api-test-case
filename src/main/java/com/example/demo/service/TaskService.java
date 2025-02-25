package com.example.demo.service;

import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.Comment;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.schema.CommentResponse;
import com.example.demo.schema.TaskRequest;
import com.example.demo.schema.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    public TaskResponse createTask(TaskRequest taskRequest, Long authorId, Long assigneeId) {
        User author;
        User assignee;

        if (!authorId.equals(assigneeId)) {
            author = userRepository.findById(authorId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
        } else {
            author = userRepository.findById(authorId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            assignee = author;
        }

        Task task = new Task();
        task.setAuthor(author);
        task.setAssignee(assignee);
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setPriority(taskRequest.getPriority());
        task.setStatus(taskRequest.getStatus());
        taskRepository.save(task);
        List<CommentResponse> commentResponseList = new ArrayList<>();
        for (Comment comment : task.getComments()) {
            commentResponseList.add(CommentResponse.builder()
                            .id(comment.getId())
                            .text(comment.getText())
                            .taskTitle(comment.getTask().getTitle())
                            .authorUsername(comment.getAuthor().getUsername())
                            .createdAt(comment.getCreatedAt())
                            .build());
        }
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .authorUsername(task.getAuthor().getUsername())
                .assigneeUsername(task.getAssignee().getUsername())
                .comments(commentResponseList)
                .build();
    }

    public TaskResponse updateTask(Long taskId, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        taskRepository.save(task);

        List<CommentResponse> commentResponseList = new ArrayList<>();
        for (Comment comment : task.getComments()) {
            commentResponseList.add(CommentResponse.builder()
                    .id(comment.getId())
                    .text(comment.getText())
                    .taskTitle(comment.getTask().getTitle())
                    .authorUsername(comment.getAuthor().getUsername())
                    .createdAt(comment.getCreatedAt())
                    .build());
        }

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .authorUsername(task.getAuthor().getUsername())
                .assigneeUsername(task.getAssignee().getUsername())
                .comments(commentResponseList)
                .build();
    }

    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        List<CommentResponse> commentResponseList = new ArrayList<>();
        for (Comment comment : task.getComments()) {
            commentResponseList.add(CommentResponse.builder()
                    .id(comment.getId())
                    .text(comment.getText())
                    .taskTitle(comment.getTask().getTitle())
                    .authorUsername(comment.getAuthor().getUsername())
                    .createdAt(comment.getCreatedAt())
                    .build());
        }
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .authorUsername(task.getAuthor().getUsername())
                .assigneeUsername(task.getAssignee().getUsername())
                .comments(commentResponseList)
                .build();
    }

    public void deleteTaskById(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public List<TaskResponse> getTasksByAssigneeId(Long assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId);
    }

    public List<TaskResponse> getTasksByAuthorId(Long authorId) {
        return taskRepository.findByAuthorId(authorId);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAllProjectedTasks().stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getPriority(),
                        task.getAuthorUsername(),
                        task.getAssigneeUsername(),
                        task.getAllComments()
                ))
                .collect(Collectors.toList());
    }
}

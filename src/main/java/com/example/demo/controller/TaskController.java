package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.schema.TaskRequest;
import com.example.demo.schema.TaskResponse;
import com.example.demo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest taskRequest,
                                                   @RequestParam Long authorId,
                                                   @RequestParam Long assigneeId) {
        TaskResponse task = taskService.createTask(taskRequest, authorId, assigneeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskResponse taskResponse;
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            taskResponse = taskService.getTaskById(id);
            if (taskResponse.getAuthorUsername().equals(currentUser.getUsername())) {
                taskService.updateTask(id, taskRequest);
                return ResponseEntity.ok(taskResponse);
            }
        }
        taskResponse = taskService.updateTask(id, taskRequest);
        return ResponseEntity.ok(taskResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        TaskResponse taskResponse = taskService.getTaskById(id);
        return ResponseEntity.ok(taskResponse);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/author")
    public ResponseEntity<List<TaskResponse>> getAllTasksByAuthorId(@RequestParam Long authorId) {
        List<TaskResponse> tasks = taskService.getTasksByAuthorId(authorId);
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/assignee")
    public ResponseEntity<List<TaskResponse>> getAllTasksByAssigneeId(@RequestParam Long assigneeId) {
        List<TaskResponse> tasks = taskService.getTasksByAssigneeId(assigneeId);
        return ResponseEntity.ok(tasks);
    }
}

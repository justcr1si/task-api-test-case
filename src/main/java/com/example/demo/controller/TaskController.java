package com.example.demo.controller;

import com.example.demo.models.Task;
import com.example.demo.schema.TaskRequest;
import com.example.demo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest taskRequest,
                                           @RequestParam Long authorId,
                                           @RequestParam Long assigneeId) {
        Task task = taskService.createTask(taskRequest, authorId, assigneeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        Task task = taskService.updateTask(id, taskRequest);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/author")
    public ResponseEntity<List<Task>> getAllTasksByAuthorId(@RequestParam Long authorId) {
        List<Task> tasks = taskService.getTasksByAuthorId(authorId);
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/assignee")
    public ResponseEntity<List<Task>> getAllTasksByAssigneeId(@RequestParam Long assigneeId) {
        List<Task> tasks = taskService.getTasksByAssigneeId(assigneeId);
        return ResponseEntity.ok(tasks);
    }
}

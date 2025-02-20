package com.example.demo.controller;

import com.example.demo.models.Task;
import com.example.demo.schema.TaskRequest;
import com.example.demo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest taskRequest,
                                           @RequestParam Long authorId,
                                           @RequestParam Long assigneeId) {
        Task task = taskService.createTask(taskRequest, authorId, assigneeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        Task task = taskService.updateTask(id, taskRequest);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/assignee/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Task>> getTasksByAssignee(@PathVariable Long id) {
        List<Task> tasks = taskService.getTasksByAssignee(id);
        return ResponseEntity.ok(tasks);
    }
}

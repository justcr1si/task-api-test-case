package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.schema.TaskRequest;
import com.example.demo.schema.TaskResponse;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Контроллер тасок")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Создание таски по TaskRequest, authorId, assigneeId")
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest taskRequest,
                                                   @RequestParam Long authorId,
                                                   @RequestParam Long assigneeId) {
        TaskResponse task = taskService.createTask(taskRequest, authorId, assigneeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Обновление таски по ID и TaskRequest")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);
        TaskResponse taskResponse;
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            taskResponse = taskService.getTaskById(id);
            if (taskResponse.getAuthorUsername().equals(currentUser.getUsername())) {
                taskService.updateTask(id, taskRequest);
                return ResponseEntity.ok(taskResponse);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        taskResponse = taskService.updateTask(id, taskRequest);
        return ResponseEntity.ok(taskResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Получение таски по ID")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        TaskResponse taskResponse = taskService.getTaskById(id);
        return ResponseEntity.ok(taskResponse);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление таски по ID")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        TaskResponse taskResponse;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            taskResponse = taskService.getTaskById(id);
            if (taskResponse.getAuthorUsername().equals(currentUser.getUsername())) {
                taskService.deleteTaskById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    @Operation(summary = "Получение всех тасок")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/author")
    @Operation(summary = "Получение всех тасок по authorId")
    public ResponseEntity<List<TaskResponse>> getAllTasksByAuthorId(@RequestParam Long authorId) {
        List<TaskResponse> tasks = taskService.getTasksByAuthorId(authorId);
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/assignee")
    @Operation(summary = "Получение всех тасок по assigneeId")
    public ResponseEntity<List<TaskResponse>> getAllTasksByAssigneeId(@RequestParam Long assigneeId) {
        List<TaskResponse> tasks = taskService.getTasksByAssigneeId(assigneeId);
        return ResponseEntity.ok(tasks);
    }
}

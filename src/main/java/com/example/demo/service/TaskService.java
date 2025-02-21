package com.example.demo.service;

import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.schema.TaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    public Task createTask(TaskRequest taskRequest, Long authorId, Long assigneeId) {
        User author = userRepository.findById(Math.toIntExact(authorId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        User assignee = userRepository.findById(Math.toIntExact(assigneeId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Task task = new Task();
        task.setAuthor(author);
        task.setAssignee(assignee);
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setPriority(taskRequest.getPriority());
        task.setStatus(taskRequest.getStatus());
        taskRepository.save(task);
        return task;
    }

    public Task updateTask(Long taskId, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());

        return taskRepository.save(task);
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    public void deleteTaskById(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public List<Task> getTasksByAssigneeId(Long assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId);
    }

    public List<Task> getTasksByAuthorId(Long authorId) {
        return taskRepository.findByAuthorId(authorId);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}

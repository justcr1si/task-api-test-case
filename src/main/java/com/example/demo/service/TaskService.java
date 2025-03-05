package com.example.demo.service;

import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.Comment;
import com.example.demo.models.Task;
import com.example.demo.models.TaskStatus;
import com.example.demo.models.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.schema.CommentResponse;
import com.example.demo.schema.TaskRequest;
import com.example.demo.schema.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Таск-сервис по обработке комментариев
 */
@Service
@RequiredArgsConstructor
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Метод по созданию таски
     * @param taskRequest
     * @param authorId
     * @param assigneeId
     * @return
     */
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

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .authorUsername(task.getAuthor().getUsername())
                .assigneeUsername(task.getAssignee().getUsername())
                .comments(task.getComments()
                        .stream()
                        .map(comment -> new CommentResponse(
                                comment.getId(),
                                comment.getText(),
                                comment.getTask().getTitle(),
                                comment.getAuthor().getUsername(),
                                comment.getCreatedAt()
                        )).collect(Collectors.toList())
                )
                .build();
    }

    /**
     * Метод по обновлению таски
     * @param taskId
     * @param taskRequest
     * @return
     */
    public TaskResponse updateTask(Long taskId, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        taskRepository.save(task);

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .authorUsername(task.getAuthor().getUsername())
                .assigneeUsername(task.getAssignee().getUsername())
                .comments(task.getComments()
                        .stream()
                        .map(comment -> new CommentResponse(
                                comment.getId(),
                                comment.getText(),
                                comment.getTask().getTitle(),
                                comment.getAuthor().getUsername(),
                                comment.getCreatedAt()
                        )).collect(Collectors.toList())
                )
                .build();
    }

    /**
     * Метод по получению таски по ее айдишнику
     * @param taskId
     * @return
     */
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

    /**
     * Метод по удалению таски по ее айдишнику
     * @param taskId
     */
    public void deleteTaskById(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    /**
     * Метод по получению тасок по айдишнику исполнителя
     * @param assigneeId
     * @return
     */
    public List<TaskResponse> getTasksByAssigneeId(Long assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId);
    }

    /**
     * Метод по получению тасок по айдишнику автора
     * @param authorId
     * @return
     */
    public List<TaskResponse> getTasksByAuthorId(Long authorId) {
        return taskRepository.findByAuthorId(authorId);
    }

    /**
     * Метод по получению всех тасок
     * @return
     */
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getPriority(),
                        task.getAuthor().getUsername(),
                        task.getAssignee().getUsername(),
                        task.getComments().stream()
                                .map(comment -> new CommentResponse(
                                        comment.getId(),
                                        comment.getText(),
                                        comment.getTask().getTitle(),
                                        comment.getAuthor().getUsername(),
                                        comment.getCreatedAt()
                                )).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    /**
     * Метод, осуществляющий пагинацию и сортировку по айдишнику в убывающем порядке
     * @param page
     * @param size
     * @return
     */
    public Page<TaskResponse> getTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Task> taskPage = taskRepository.findAll(pageable);

        return getTaskPage(taskPage);
    }

    /**
     * Метод по получению тасок относительно статуса с пагинацией
     * @param status
     * @param page
     * @param size
     * @return
     */
    public Page<TaskResponse> getTasksByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        TaskStatus taskStatus = TaskStatus.valueOf(status);
        Page<Task> taskPage = taskRepository.findByStatus(taskStatus, pageable);

        return getTaskPage(taskPage);
    }

    /**
     * Метод по получению пейджа, содержащий TaskResponse
     * @param taskPage
     * @return
     */
    private Page<TaskResponse> getTaskPage(Page<Task> taskPage) {
        return taskPage.map(task -> new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getAuthor().getUsername(),
                task.getAssignee().getUsername(),
                task.getComments().stream()
                        .map(comment -> new CommentResponse(
                                comment.getId(),
                                comment.getText(),
                                comment.getTask().getTitle(),
                                comment.getAuthor().getUsername(),
                                comment.getCreatedAt()
                        )).collect(Collectors.toList())
        ));
    }
}

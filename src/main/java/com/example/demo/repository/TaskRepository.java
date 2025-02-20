package com.example.demo.repository;

import com.example.demo.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByAuthorId(Long authorId);
}

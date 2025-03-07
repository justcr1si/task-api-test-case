package com.example.demo.repository;

import com.example.demo.models.Task;
import com.example.demo.models.TaskStatus;
import com.example.demo.schema.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<TaskResponse> findByAssigneeId(Long assigneeId);

    List<TaskResponse> findByAuthorId(Long authorId);

    @Query("SELECT t FROM Task t WHERE t.status = :status")
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
}

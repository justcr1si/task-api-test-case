package com.example.demo.repository;

import com.example.demo.models.Task;
import com.example.demo.schema.TaskProjection;
import com.example.demo.schema.TaskResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<TaskResponse> findByAssigneeId(Long assigneeId);
    List<TaskResponse> findByAuthorId(Long authorId);
    @Query("SELECT t.id AS id, t.title AS title, t.description AS description, " +
            "t.status AS status, t.priority AS priority, t.author.username AS authorUsername, " +
            "t.assignee.username AS assigneeUsername " +
            "FROM Task t")
    List<TaskProjection> findAllProjectedTasks();
}

package com.example.demo.schema;

import com.example.demo.models.TaskPriority;
import com.example.demo.models.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "Ответ с информацией о таске")
@Data
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    private String authorUsername;
    private String assigneeUsername;
    private List<CommentResponse> comments;
}

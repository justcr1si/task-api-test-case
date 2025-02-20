package com.example.demo.schema;

import com.example.demo.models.TaskPriority;
import com.example.demo.models.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Schema(description = "Таск-запрос, содержащий основные поля при создании таски")
@Data
public class TaskRequest {
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
}

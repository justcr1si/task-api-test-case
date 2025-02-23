package com.example.demo.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Таск-ответ, содержащий необходимые поля")
@Data
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String text;
    private String taskTitle;
    private String authorUsername;
    private LocalDateTime createdAt;
}

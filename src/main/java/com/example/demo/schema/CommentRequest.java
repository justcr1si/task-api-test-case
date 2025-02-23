package com.example.demo.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Коммент-запрос, содержащий основные поля")
@Data
@AllArgsConstructor
public class CommentRequest {
    private String text;
}

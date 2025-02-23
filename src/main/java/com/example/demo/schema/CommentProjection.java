package com.example.demo.schema;

import java.time.LocalDateTime;

public interface CommentProjection {
    Long getId();

    String getText();

    String getTaskTitle();

    String getAuthorUsername();

    LocalDateTime getCreatedAt();
}

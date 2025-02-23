package com.example.demo.schema;

import com.example.demo.models.Comment;
import com.example.demo.models.TaskPriority;
import com.example.demo.models.TaskStatus;

import java.util.List;

public interface TaskProjection {
    Long getId();

    String getTitle();

    String getDescription();

    TaskStatus getStatus();

    TaskPriority getPriority();

    String getAuthorUsername();

    String getAssigneeUsername();

    List<CommentResponse> getAllComments();
}

package com.example.demo.repository;

import com.example.demo.models.Comment;
import com.example.demo.schema.CommentProjection;
import com.example.demo.schema.CommentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<CommentResponse> findByTaskId(Long taskId);
    @Query("SELECT c.id AS id, c.text AS text, " +
            "c.createdAt AS createdAt, c.author.username AS authorUsername " +
            "FROM Comment c")
    List<CommentProjection> findAllProjectedComments();
}

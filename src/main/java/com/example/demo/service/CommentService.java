package com.example.demo.service;

import com.example.demo.exceptions.CommentNotFoundException;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.Comment;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.schema.CommentRequest;
import com.example.demo.schema.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис по обработке комментариев
 */
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    /**
     * Метод по созданию комментария
     * @param commentRequest
     * @param authorId
     * @param taskId
     * @return
     */
    public CommentResponse addComment(CommentRequest commentRequest, Long authorId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .text(commentRequest.getText())
                .task(task)
                .author(author)
                .build();

        commentRepository.save(comment);

        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorUsername(author.getUsername())
                .taskTitle(task.getTitle())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    /**
     * Метод по получению всех комментариев
     * @return
     */
    public List<CommentResponse> getAllComments() {
        return commentRepository.findAll().stream()
                .map(comment -> CommentResponse.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .taskTitle(comment.getTask().getTitle())
                        .authorUsername(comment.getAuthor().getUsername())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Метод по получению комментариев по айдишнику таски
     * @param taskId
     * @return
     */
    public List<CommentResponse> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    /**
     * Метод по удалению комментария по айдишнику
     * @param commentId
     */
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    /**
     * Метод по обновлению комментария
     * @param commentId
     * @param commentRequest
     * @return
     */
    public CommentResponse updateComment(Long commentId, CommentRequest commentRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        comment.setText(commentRequest.getText());
        commentRepository.save(comment);
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorUsername(comment.getAuthor().getUsername())
                .taskTitle(comment.getTask().getTitle())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    /**
     * Пагинация комментариев
     * @param page
     * @param size
     * @return
     */
    public Page<CommentResponse> getComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Comment> taskPage = commentRepository.findAll(pageable);

        return getCommentPage(taskPage);
    }

    /**
     * Метод по получению комментария по его айдишнику
     * @param commentId
     * @return
     */
    public CommentResponse getCommentById(Long commentId) {
        return commentRepository.findCommentsById(commentId);
    }

    /**
     * Приватный метод по получению пейджа, содержащий CommentResponse
     * @param commentPage
     * @return
     */
    private Page<CommentResponse> getCommentPage(Page<Comment> commentPage) {
        return commentPage.map(comment -> new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getTask().getTitle(),
                comment.getAuthor().getUsername(),
                comment.getCreatedAt()
        ));
    }
}

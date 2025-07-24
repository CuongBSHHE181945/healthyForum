package com.healthyForum.service.post;

import com.healthyForum.model.Post.Comment;
import com.healthyForum.model.User;
import com.healthyForum.repository.Post.CommentRepository;
import com.healthyForum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
    public class CommentService {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<Comment> getCommentsByPostId(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentRepository.findByPostIdWithUser(postId, pageable);
    }

        public void saveComment(Comment comment) {
            commentRepository.save(comment);
        }

    public void updateComment(Long commentId, String newContent, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Comment comment = commentRepository.findByIdAndUserId(commentId, user.getId())
                .orElseThrow(() -> new RuntimeException("You don't have permission to edit this comment!"));

        comment.setContent(newContent);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public Long getPostIdByCommentId(Long commentId) {
        return commentRepository.findByIdWithPost(commentId)
                .map(comment -> comment.getPost().getId())
                .orElseThrow(() -> new RuntimeException("Comment or Post not found!"));
    }

    public Optional<Comment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public Long deleteComment(Long commentId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findByIdAndUserId(commentId, user.getId())
                .orElseThrow(() -> new RuntimeException("You dont have the permission to delete this comment"));

        Long postId = comment.getPost().getId();
        commentRepository.delete(comment);
        return postId;
    }

    }



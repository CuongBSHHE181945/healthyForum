package com.healthyForum.service.post;

import com.healthyForum.model.Post.Comment;
import com.healthyForum.repository.Post.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
    public class CommentService {

    private static final int PAGE_SIZE = 10;

    @Autowired
        private CommentRepository commentRepository;

        public Page<Comment> getCommentsByPostId(Long postId, int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);
        }

        public void saveComment(Comment comment) {
            commentRepository.save(comment);
        }
    }



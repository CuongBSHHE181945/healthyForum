package com.healthyForum.repository.Post;

import com.healthyForum.model.Post.Comment;
import com.healthyForum.model.Post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

}
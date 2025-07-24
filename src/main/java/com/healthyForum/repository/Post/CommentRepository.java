package com.healthyForum.repository.Post;

import com.healthyForum.model.Post.Comment;
import com.healthyForum.model.Post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

//    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.id = :postId")
//    List<Comment> findByPostIdWithUser(@Param("postId") Long postId);

    Optional<Comment> findByIdAndUserId(Long commentId, Long userId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    Page<Comment> findByPostIdWithUser(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.id = :commentId")
    Optional<Comment> findByIdWithPost(@Param("commentId") Long commentId);
}
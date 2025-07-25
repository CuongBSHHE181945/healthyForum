package com.healthyForum.repository.Post;

import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.model.Post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findVisibleByVisibility(Visibility visibility);

    List<Post> findByUserId(Long userId);

    List<Post> findByUserIdAndVisibility(Long userId, Visibility visibility);

    List<Post> findByVisibilityAndBannedFalse(Visibility visibility);

    Page<Post> findByVisibilityAndBannedFalse(Visibility visibility, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isDraft = false AND p.banned = false AND p.visibility = :visibility AND (LOWER(p.title) LIKE :pattern OR LOWER(p.content) LIKE :pattern)")
    Page<Post> searchPosts(@Param("pattern") String pattern, @Param("visibility") Visibility visibility, Pageable pageable);
    List<Post> findByUserIdAndVisibilityNot(Long userId, Visibility excludedVisibility);

    @Query(value = "SELECT p.* FROM post p " +
           "LEFT JOIN post_reaction r ON r.post_id = p.post_id AND r.reaction_type = 'LIKE' " +
           "LEFT JOIN comment c ON c.post_id = p.post_id " +
           "WHERE p.created_at >= NOW() - INTERVAL :days DAY " +
           "AND p.visibility = 'PUBLIC' " +
           "AND p.banned = FALSE " +
           "AND p.is_draft = FALSE " +
           "GROUP BY p.post_id " +
           "ORDER BY (COUNT(DISTINCT r.id) * 2 + COUNT(DISTINCT c.comment_id)) DESC, p.created_at DESC",
           nativeQuery = true)
    List<Post> findTrendingPosts(@Param("days") int days, Pageable pageable);

    @Query(value = "SELECT p.* FROM post p " +
           "LEFT JOIN post_reaction r ON r.post_id = p.post_id AND r.reaction_type = 'LIKE' " +
           "LEFT JOIN comment c ON c.post_id = p.post_id " +
           "WHERE p.visibility = 'PUBLIC' " +
           "AND p.banned = FALSE " +
           "AND p.is_draft = FALSE " +
           "GROUP BY p.post_id " +
           "ORDER BY (COUNT(DISTINCT r.id) * 2 + COUNT(DISTINCT c.comment_id)) DESC, p.updated_at DESC",
           nativeQuery = true)
    List<Post> findTrendingPostsSimple(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id IN :userIds AND p.isDraft = false AND p.banned = false AND p.visibility = 'PUBLIC' ORDER BY p.createdAt DESC")
    Page<Post> findFollowingPosts(@Param("userIds") List<Long> userIds, Pageable pageable);
}
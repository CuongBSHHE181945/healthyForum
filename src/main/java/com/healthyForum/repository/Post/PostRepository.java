package com.healthyForum.repository.Post;

import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.model.Post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findVisibleByVisibility(Visibility visibility);

    List<Post> findByUserId(Long userId);

    List<Post> findByUserIdAndVisibility(Long userId, Visibility visibility);

    List<Post> findByVisibilityAndBannedFalse(Visibility visibility);

    @Query("SELECT p FROM Post p WHERE p.isDraft = false AND p.banned = false AND p.visibility = :visibility AND (LOWER(p.title) LIKE :pattern OR p.content LIKE :pattern) ORDER BY p.id DESC")
    List<Post> searchPosts(@Param("pattern") String pattern, @Param("visibility") Visibility visibility, org.springframework.data.domain.Pageable pageable);
    List<Post> findByUserIdAndVisibilityNot(Long userId, Visibility excludedVisibility);


}
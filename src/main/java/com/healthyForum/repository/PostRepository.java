package com.healthyForum.repository;

import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findVisibleByVisibility(Visibility visibility);

    List<Post> findByUserId(Long userId);

    List<Post> findByUserIdAndVisibility(Long userId, Visibility visibility);

    List<Post> findByVisibilityAndBannedFalse(Visibility visibility);

    List<Post> findByUserIdAndVisibilityNot(Long userId, Visibility excludedVisibility);
}
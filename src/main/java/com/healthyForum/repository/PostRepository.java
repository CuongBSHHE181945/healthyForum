package com.healthyForum.repository;

import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findVisibleByVisibility(Visibility visibility);

    List<Post> findByUserId(Long userId);

    List<Post> findByUserIdAndIsDraftTrue(Long userId);

    List<Post> findByIsDraftFalseAndVisibilityAndBannedFalse(Visibility visibility);

}
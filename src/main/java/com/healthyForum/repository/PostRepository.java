package com.healthyForum.repository;

import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findVisibleByVisibility(Visibility visibility);

    List<Post> findByUserUsername(String username);

    List<Post> findByUserUsernameAndIsDraftTrue(String username);

    List<Post> findByIsDraftFalseAndVisibilityAndBannedFalse(Visibility visibility);

}
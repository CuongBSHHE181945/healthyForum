package com.healthyForum.repository;

import com.healthyForum.model.Post;
import com.healthyForum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Find posts by user
    List<Post> findByUser(User user);

    // Find public posts that aren't drafts
    List<Post> findByVisibilityAndIsDraftFalseOrderByCreatedAtDesc(Post.Visibility visibility);

    // Find all posts by visibility
    List<Post> findByVisibility(Post.Visibility visibility);

    // Find posts with title containing the search term
    List<Post> findByTitleContainingIgnoreCaseAndIsDraftFalse(String searchTerm);

    // Find posts with content containing the search term
    List<Post> findByContentContainingIgnoreCaseAndIsDraftFalse(String searchTerm);

    // Find posts for a specific user that are drafts
    List<Post> findByUserAndIsDraftTrue(User user);
    // Find recent posts (public, non-draft)
    List<Post> findTop10ByVisibilityAndIsDraftFalseOrderByCreatedAtDesc(Post.Visibility visibility);
    // Update in PostRepository.java
    List<Post> findByVisibilityAndIsDraftFalseAndBannedFalseOrderByCreatedAtDesc(Post.Visibility visibility);

}
package com.healthyForum.repository.Post;

import com.healthyForum.model.Enum.ReactionType;
import com.healthyForum.model.Post.Post;
import com.healthyForum.model.Post.PostReaction;
import com.healthyForum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    Optional<PostReaction> findByUserAndPost(User user, Post post);
    Long countByPostAndType(Post post, ReactionType type);

}

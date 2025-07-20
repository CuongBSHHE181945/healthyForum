package com.healthyForum.repository;

import com.healthyForum.model.Follow;
import com.healthyForum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowed(User follower, User followed);

    void deleteByFollowerAndFollowed(User follower, User followed);

    List<Follow> findByFollower(User user);

    List<Follow> findByFollowed(User user);

    long countByFollowed(User user);

    long countByFollower(User user);
}

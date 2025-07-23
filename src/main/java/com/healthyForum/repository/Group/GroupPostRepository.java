package com.healthyForum.repository.Group;

import com.healthyForum.model.Group.Group;
import com.healthyForum.model.Group.GroupPost;
import com.healthyForum.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {

    // Find posts by group
    Page<GroupPost> findByGroupAndIsActiveTrueOrderByCreatedAtDesc(Group group, Pageable pageable);

    // Find active post by ID
    Optional<GroupPost> findByIdAndIsActiveTrue(Long id);

    @Query("SELECT p FROM GroupPost p " +
            "JOIN FETCH p.author a " +
            "JOIN FETCH a.account acc " +
            "JOIN FETCH p.group g " +
            "WHERE p.id = :id AND p.isActive = true")
    Optional<GroupPost> findByIdAndIsActiveTrueWithFullDetails(@Param("id") Long id);


    @Query("SELECT p FROM GroupPost p " +
            "JOIN FETCH p.author a " +
            "LEFT JOIN FETCH a.account acc " +
            "JOIN FETCH p.group g " +
            "WHERE p.id = :id AND p.isActive = true")
    Optional<GroupPost> findByIdAndIsActiveTrueWithDetails(@Param("id") Long id);
}
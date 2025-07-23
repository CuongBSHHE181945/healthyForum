package com.healthyForum.repository.Group;

import com.healthyForum.model.Group.Group;
import com.healthyForum.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    // Find active groups
    List<Group> findByIsActiveTrueOrderByCreatedAtDesc();

    // Find group by ID and active status
    Optional<Group> findByIdAndIsActiveTrue(Long id);

    // Search groups by name or description
    @Query("SELECT g FROM Group g WHERE g.isActive = true AND " +
            "(LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY g.createdAt DESC")
    List<Group> searchGroups(@Param("keyword") String keyword);

    // Sửa từ 'createdBy.username' thành 'createdBy.account.username'
    @Query("SELECT g FROM Group g WHERE g.createdBy.account.username = :username AND g.isActive = true ORDER BY g.createdAt DESC")
    List<Group> findByCreatorUsername(@Param("username") String username);

    // Sửa từ 'user.username' thành 'user.account.username'
    @Query("SELECT DISTINCT gm.group FROM GroupMember gm WHERE gm.user.account.username = :username AND gm.group.isActive = true ORDER BY gm.group.createdAt DESC")
    List<Group> findGroupsByMemberUsername(@Param("username") String username);
}
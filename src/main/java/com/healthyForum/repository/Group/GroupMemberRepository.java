package com.healthyForum.repository.Group;

import com.healthyForum.model.Group.Group;
import com.healthyForum.model.Group.GroupMember;
import com.healthyForum.model.Group.GroupRole;
import com.healthyForum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // Check if user is member of a group
    boolean existsByGroupAndUser(Group group, User user);

    // Find member by group and user
    Optional<GroupMember> findByGroupAndUser(Group group, User user);

    // Find all members of a group
    List<GroupMember> findByGroupOrderByJoinedAtAsc(Group group);

    // Find members by role in a group
    List<GroupMember> findByGroupAndRole(Group group, GroupRole role);

    // Find group admins
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group = :group AND gm.role = 'ADMIN'")
    List<GroupMember> findGroupAdmins(@Param("group") Group group);

    // Check if user has admin or moderator role in group
    @Query("SELECT COUNT(gm) > 0 FROM GroupMember gm WHERE gm.group = :group AND gm.user = :user AND (gm.role = 'ADMIN' OR gm.role = 'MODERATOR')")
    boolean hasAdminOrModeratorRole(@Param("group") Group group, @Param("user") User user);
}
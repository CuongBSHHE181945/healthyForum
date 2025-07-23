package com.healthyForum.service.Group;

import com.healthyForum.model.Group.Group;
import com.healthyForum.model.Group.GroupMember;
import com.healthyForum.model.Group.GroupRole;
import com.healthyForum.model.User;
import com.healthyForum.repository.Group.GroupRepository;
import com.healthyForum.repository.Group.GroupMemberRepository;
import com.healthyForum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserService userService;

    // Create new group
    public Group createGroup(String name, String description, Principal principal) {
        // Lấy User entity từ database
        User creator = userService.getCurrentUser(principal);
        if (creator == null) {
            throw new RuntimeException("User not found");
        }
        Group group = new Group(name, description, creator);
        group = groupRepository.save(group);

        // Add creator as admin
        GroupMember adminMember = new GroupMember(group, creator, GroupRole.ADMIN);
        groupMemberRepository.save(adminMember);

        // Update member count
        group.setMemberCount(1);
        return groupRepository.save(group);
    }

    // Get all active groups
    @Transactional(readOnly = true)
    public List<Group> getAllActiveGroups() {
        return groupRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    // Get group by ID
    @Transactional(readOnly = true)
    public Optional<Group> getGroupById(Long id) {
        return groupRepository.findByIdAndIsActiveTrue(id);
    }

    // Search groups
    @Transactional(readOnly = true)
    public List<Group> searchGroups(String keyword) {
        return groupRepository.searchGroups(keyword);
    }


    // Update group - only admins can edit
    public Group updateGroup(Long groupId, String name, String description, User user) {
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if user has ADMIN role (not moderator)
        if (!hasAdminRole(group, user)) {
            throw new RuntimeException("Only admins can edit groups");
        }

        group.setName(name);
        group.setDescription(description);
        return groupRepository.save(group);
    }

    // Delete group - only admins can delete
    public void deleteGroup(Long groupId, User user) {
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if user has ADMIN role (not moderator)
        if (!hasAdminRole(group, user)) {
            throw new RuntimeException("Only admins can delete groups");
        }

        group.setIsActive(false);
        groupRepository.save(group);
    }

    // Check if user has admin role in group (exact admin role, not moderator)
    private boolean hasAdminRole(Group group, User user) {
        return groupMemberRepository.findByGroupAndUser(group, user)
                .map(member -> member.getRole() == GroupRole.ADMIN)
                .orElse(false);
    }


    public List<Group> getGroupsCreatedByUser(String username) {
        return groupRepository.findByCreatorUsername(username);
    }

    public List<Group> getGroupsUserJoined(String username) {
        return groupRepository.findGroupsByMemberUsername(username);
    }
}
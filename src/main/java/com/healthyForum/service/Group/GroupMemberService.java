package com.healthyForum.service.Group;

import com.healthyForum.model.Group.Group;
import com.healthyForum.model.Group.GroupMember;
import com.healthyForum.model.Group.GroupRole;
import com.healthyForum.model.User;
import com.healthyForum.repository.Group.GroupRepository;
import com.healthyForum.repository.Group.GroupMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupMemberService {

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupRepository groupRepository;

    // Join group
    public GroupMember joinGroup(Long groupId, User user) {
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if user is already a member
        if (groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new RuntimeException("User is already a member of this group");
        }

        GroupMember member = new GroupMember(group, user, GroupRole.MEMBER);
        member = groupMemberRepository.save(member);

        // Update member count
        group.setMemberCount(group.getMemberCount() + 1);
        groupRepository.save(group);

        return member;
    }

    // Leave group
    public void leaveGroup(Long groupId, User user) {
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        // Check if user is the only admin
        List<GroupMember> admins = groupMemberRepository.findByGroupAndRole(group, GroupRole.ADMIN);
        if (member.getRole() == GroupRole.ADMIN && admins.size() == 1) {
            throw new RuntimeException("Cannot leave group - you are the only admin");
        }

        groupMemberRepository.delete(member);

        // Update member count
        group.setMemberCount(group.getMemberCount() - 1);
        groupRepository.save(group);
    }

    // Get group members
    @Transactional(readOnly = true)
    public List<GroupMember> getGroupMembers(Long groupId) {
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        return groupMemberRepository.findByGroupOrderByJoinedAtAsc(group);
    }


    // Check if user is member of group
    @Transactional(readOnly = true)
    public boolean isMember(Long groupId, User user) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) return false;

        return groupMemberRepository.existsByGroupAndUser(group, user);
    }

    public GroupMember changeMemberRole(Long groupId, Long memberId, GroupRole newRole, User requester) {
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if requester has admin role
        if (!hasAdminRole(group, requester)) {
            throw new RuntimeException("Only administrators can change member roles");
        }

        // Find the GroupMember directly by memberId
        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Verify the member belongs to the correct group
        if (!member.getGroup().getId().equals(groupId)) {
            throw new RuntimeException("Member does not belong to this group");
        }

        // Get current admin count
        List<GroupMember> currentAdmins = groupMemberRepository.findByGroupAndRole(group, GroupRole.ADMIN);

        // Check if this is the last admin trying to change their own role
        if (member.getRole() == GroupRole.ADMIN &&
                member.getUser().getId().equals(requester.getId()) &&
                currentAdmins.size() == 1 &&
                newRole != GroupRole.ADMIN) {
            throw new RuntimeException("Cannot change role - you are the last admin of the group. Please assign another admin before changing your role.");
        }

        // Additional check: prevent removing the last admin
        if (member.getRole() == GroupRole.ADMIN &&
                newRole != GroupRole.ADMIN &&
                currentAdmins.size() == 1) {
            throw new RuntimeException("Roles cannot be changed - the group must have at least one admin.");
        }

        member.setRole(newRole);
        return groupMemberRepository.save(member);
    }

    // Add this method to GroupMemberService.java

    public void removeMemberWithPermissionCheck(Long groupId, Long memberId, User requester) {
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Get requester's role
        GroupMember requesterMember = groupMemberRepository.findByGroupAndUser(group, requester)
                .orElseThrow(() -> new RuntimeException("You are not a member of this group"));

        // Get target member
        GroupMember targetMember = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Verify the member belongs to the correct group
        if (!targetMember.getGroup().getId().equals(groupId)) {
            throw new RuntimeException("Member does not belong to this group");
        }

        // Permission logic
        GroupRole requesterRole = requesterMember.getRole();
        GroupRole targetRole = targetMember.getRole();

        if (requesterRole == GroupRole.ADMIN) {
            // Admins can remove anyone except themselves if they're the last admin
            if (targetRole == GroupRole.ADMIN) {
                List<GroupMember> admins = groupMemberRepository.findByGroupAndRole(group, GroupRole.ADMIN);
                if (admins.size() == 1 && targetMember.getUser().getId().equals(requester.getId())) {
                    throw new RuntimeException("Cannot remove last administrator");
                }
            }
        } else if (requesterRole == GroupRole.MODERATOR) {
            // Moderators can only remove regular members
            if (targetRole != GroupRole.MEMBER) {
                throw new RuntimeException("Moderators can only remove regular members.");
            }
        } else {
            throw new RuntimeException("You do not have the right to remove members.");
        }

        // Prevent self-removal
        if (targetMember.getUser().getId().equals(requester.getId())) {
            throw new RuntimeException("Can't get rid of yourself");
        }

        groupMemberRepository.delete(targetMember);

        // Update member count
        group.setMemberCount(group.getMemberCount() - 1);
        groupRepository.save(group);
    }

    // Check if user has admin or moderator role
    @Transactional(readOnly = true)
    public boolean hasAdminOrModeratorRole(Group group, User user) {
        return groupMemberRepository.hasAdminOrModeratorRole(group, user);
    }



    public boolean hasAdminRole(Group group, User user) {
        return groupMemberRepository.findByGroupAndUser(group, user)
                .map(member -> member.getRole() == GroupRole.ADMIN)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Optional<GroupMember> findByGroupAndUser(Group group, User user) {
        return groupMemberRepository.findByGroupAndUser(group, user);
    }
}
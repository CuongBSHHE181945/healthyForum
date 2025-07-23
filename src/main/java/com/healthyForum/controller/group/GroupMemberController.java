package com.healthyForum.controller.group;

import com.healthyForum.model.Group.Group;
import com.healthyForum.model.Group.GroupMember;
import com.healthyForum.model.Group.GroupRole;
import com.healthyForum.model.User;
import com.healthyForum.service.Group.GroupMemberService;
import com.healthyForum.service.Group.GroupService;
import com.healthyForum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/groups/{groupId}/members")
public class GroupMemberController {

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    // View group members
    @GetMapping
    public String getGroupMembers(@PathVariable Long groupId, Model model) {
        try {
            List<GroupMember> members = groupMemberService.getGroupMembers(groupId);
            Group group = groupService.getGroupById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            model.addAttribute("members", members);
            model.addAttribute("group", group);
            return "group/member/list";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Error loading member list: " + e.getMessage());
            return "error/error";
        }
    }

    // Join group
    @PostMapping("/join")
    public String joinGroup(@PathVariable Long groupId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getCurrentUser(principal);
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please login to join the group");
                return "redirect:/auth/login";
            }

            groupMemberService.joinGroup(groupId, user);
            redirectAttributes.addFlashAttribute("successMessage", "Joined group successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error joining group: " + e.getMessage());
        }
        return "redirect:/groups/" + groupId;
    }

    // Leave group
    @PostMapping("/leave")
    public String leaveGroup(@PathVariable Long groupId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getCurrentUser(principal);
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please login");
                return "redirect:/auth/login";
            }

            groupMemberService.leaveGroup(groupId, user);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully left the group!");
            return "redirect:/groups";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error when leaving group: " + e.getMessage());
            return "redirect:/groups/" + groupId;
        }
    }

    // Manage members (Admin/Moderator only)
    @GetMapping("/manage")
    public String manageMembers(@PathVariable Long groupId, Principal principal, Model model) {
        try {
            User currentUser = userService.getCurrentUser(principal);
            if (currentUser == null) {
                return "redirect:/auth/login";
            }

            Group group = groupService.getGroupById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            // Check if user has admin or moderator role
            if (!groupMemberService.hasAdminOrModeratorRole(group, currentUser)) {
                model.addAttribute("errorMessage", "You do not have permission to manage members");
                return "redirect:/groups/" + groupId;
            }

            List<GroupMember> members = groupMemberService.getGroupMembers(groupId);

            // Check if current user is admin (not just moderator)
            boolean isCurrentUserAdmin = groupMemberService.hasAdminRole(group, currentUser);

            // Count members by role
            Map<GroupRole, Long> membersByRole = members.stream()
                    .collect(Collectors.groupingBy(GroupMember::getRole, Collectors.counting()));

            long adminCount = membersByRole.getOrDefault(GroupRole.ADMIN, 0L);
            long moderatorCount = membersByRole.getOrDefault(GroupRole.MODERATOR, 0L);
            long memberCount = membersByRole.getOrDefault(GroupRole.MEMBER, 0L);

            model.addAttribute("group", group);
            model.addAttribute("members", members);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isCurrentUserAdmin", isCurrentUserAdmin);
            model.addAttribute("roles", GroupRole.values());
            model.addAttribute("adminCount", adminCount);
            model.addAttribute("moderatorCount", moderatorCount);
            model.addAttribute("memberCount", memberCount);

            return "group/member/manage";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Error loading admin page: " + e.getMessage());
            return "error/error";
        }
    }

    // Change member role
    @PostMapping("/{memberId}/role")
    public String changeMemberRole(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @RequestParam GroupRole role,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            User requester = userService.getCurrentUser(principal);
            if (requester == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please login");
                return "redirect:/auth/login";
            }

            groupMemberService.changeMemberRole(groupId, memberId, role, requester);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully changed roles!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error changing role: " + e.getMessage());
        }
        return "redirect:/groups/" + groupId + "/members/manage";
    }

    // Remove member
    @PostMapping("/{memberId}/remove")
    public String removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            User requester = userService.getCurrentUser(principal);
            if (requester == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please login");
                return "redirect:/auth/login";
            }

            groupMemberService.removeMemberWithPermissionCheck(groupId, memberId, requester);
            redirectAttributes.addFlashAttribute("successMessage", "Member successfully removed!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing member: " + e.getMessage());
        }
        return "redirect:/groups/" + groupId + "/members/manage";
    }
}
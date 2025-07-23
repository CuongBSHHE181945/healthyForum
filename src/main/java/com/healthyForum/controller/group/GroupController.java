package com.healthyForum.controller.group;

import com.healthyForum.model.Group.Group;
import com.healthyForum.model.Group.GroupPost;
import com.healthyForum.model.Group.GroupRole;
import com.healthyForum.model.User;
import com.healthyForum.service.Group.GroupMemberService;
import com.healthyForum.service.Group.GroupPostService;
import com.healthyForum.service.Group.GroupService;
import com.healthyForum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupPostService groupPostService;

    // Get all active groups
    @GetMapping
    public String getAllGroups(Model model) {
        List<Group> groups = groupService.getAllActiveGroups();
        if (groups == null) {
            groups = new ArrayList<>();
        }
        model.addAttribute("groups", groups);
        return "group/list";
    }


    // Show create group form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("group", new Group());
        return "group/create";
    }

    @PostMapping
    public String createGroup(
            @RequestParam String name,
            @RequestParam String description,
            Principal principal, // Thay đổi từ User thành UserDetails
            RedirectAttributes redirectAttributes) {
        try {

            Group group = groupService.createGroup(name, description, principal);
            redirectAttributes.addFlashAttribute("successMessage", "Group created successfully!");
            return "redirect:/groups/" + group.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating group: " + e.getMessage());
            return "redirect:/groups/create";
        }
    }

    @GetMapping("/{id}")
    public String getGroupDetail(@PathVariable Long id,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Principal principal,
                                 Model model) {
        try {
            Group group = groupService.getGroupById(id)
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            // Check membership and roles
            boolean isMember = false;
            boolean isAdmin = false;
            boolean isModerator = false; // Add this line
            User currentUser = null;

            if (principal != null) {
                currentUser = userService.getCurrentUser(principal);
                if (currentUser != null) {
                    isMember = groupMemberService.isMember(id, currentUser);
                    isAdmin = groupMemberService.hasAdminRole(group, currentUser);

                    // Add moderator check
                    if (!isAdmin) {
                        isModerator = groupMemberService.findByGroupAndUser(group, currentUser)
                                .map(member -> member.getRole() == GroupRole.MODERATOR)
                                .orElse(false);
                    }
                }
            }

            // Get posts with proper pagination
            Page<GroupPost> posts = null;
            try {
                Pageable pageable = PageRequest.of(page, size);
                posts = groupPostService.getGroupPosts(id, pageable);
            } catch (Exception e) {
                model.addAttribute("postsError", "Unable to load article: " + e.getMessage());
            }

            model.addAttribute("group", group);
            model.addAttribute("posts", posts);
            model.addAttribute("isMember", isMember);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isModerator", isModerator); // Add this line
            model.addAttribute("currentUser", currentUser);

            return "group/detail";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Error loading group: " + e.getMessage());
            return "error/error";
        }
    }

    // Search groups
    @GetMapping("/search")
    public String searchGroups(@RequestParam String keyword, Model model) {
        List<Group> groups = groupService.searchGroups(keyword);
        model.addAttribute("groups", groups);
        model.addAttribute("keyword", keyword);
        return "group/list";
    }


    // Đặt mapping cụ thể TRƯỚC mapping có path variable
    @GetMapping("/my-groups")
    public String getMyGroups(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        // Lấy danh sách nhóm user đã tạo
        List<Group> createdGroups = groupService.getGroupsCreatedByUser(username);

        // Lấy danh sách nhóm user đã tham gia
        List<Group> joinedGroups = groupService.getGroupsUserJoined(username);

        model.addAttribute("createdGroups", createdGroups);
        model.addAttribute("joinedGroups", joinedGroups);
        return "group/my-groups";
    }


    // Show update form
    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        return groupService.getGroupById(id)
                .map(group -> {
                    model.addAttribute("group", group);
                    return "group/edit";
                })
                .orElse("error/404");
    }

    // Update group
    @PutMapping("/{id}")
    public String updateGroup(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        try {
            Group group = groupService.updateGroup(id, name, description, user);
            redirectAttributes.addFlashAttribute("successMessage", "Group updated successfully!");
            return "redirect:/groups/" + group.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error while updating group: " + e.getMessage());
            return "redirect:/groups/" + id + "/edit";
        }
    }

    // Delete group
    @DeleteMapping("/{id}")
    public String deleteGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        try {
            groupService.deleteGroup(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Group was deleted successfully!");
            return "redirect:/groups";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error while deleting group: " + e.getMessage());
            return "redirect:/groups/" + id;
        }
    }
}
package com.healthyForum.controller.group;

import com.healthyForum.model.Group.Group;
import com.healthyForum.model.Group.GroupPost;
import com.healthyForum.model.Group.GroupPostComment;
import com.healthyForum.model.User;
import com.healthyForum.repository.Group.GroupPostCommentRepository;
import com.healthyForum.repository.Group.GroupPostRepository;
import com.healthyForum.service.Group.GroupMemberService;
import com.healthyForum.service.Group.GroupPostService;
import com.healthyForum.service.Group.GroupService;
import com.healthyForum.service.UserService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/groups/{groupId}/posts")
public class GroupPostController {

    @Autowired
    private GroupPostService groupPostService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupPostCommentRepository groupPostCommentRepository;

    @Autowired
    private GroupPostRepository groupPostRepository;

    // Show create post form
    @GetMapping("/create")
    public String showCreateForm(@PathVariable Long groupId, Model model) {
        model.addAttribute("groupId", groupId);
        return "group/posts/create";
    }

    // Update create post method
    @PostMapping
    public String createPost(
            @PathVariable Long groupId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) String imageUrl,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            GroupPost post = groupPostService.createPost(groupId, title, content,
                    imageFile, imageUrl, principal);
            redirectAttributes.addFlashAttribute("successMessage", "The article has been created successfully.!");
            return "redirect:/groups/" + groupId + "/posts/" + post.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating post: " + e.getMessage());
            return "redirect:/groups/" + groupId + "/posts/create";
        }
    }

    @GetMapping("/{postId}")
    @Transactional(readOnly = true)
    public String getPostDetail(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            Principal principal,
            Model model) {

        try {
            // Fetch post với full details
            GroupPost post = groupPostRepository.findByIdAndIsActiveTrueWithDetails(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            Group group = groupService.getGroupById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            // Fetch comments với author details
            List<GroupPostComment> comments = groupPostCommentRepository.findByPostIdWithAuthor(postId);

            User currentUser = null;
            boolean isMember = false;
            boolean canDelete = false;

            if (principal != null) {
                currentUser = userService.getCurrentUser(principal);
                if (currentUser != null) {
                    isMember = groupMemberService.isMember(groupId, currentUser);
                    canDelete = groupMemberService.hasAdminOrModeratorRole(group, currentUser);
                }
            }

            model.addAttribute("post", post);
            model.addAttribute("group", group);
            model.addAttribute("comments", comments);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isMember", isMember);
            model.addAttribute("canDelete", canDelete);

            return "group/posts/detail";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading article: " + e.getMessage());
            return "error/error";
        }
    }


    // Like post
    @PostMapping("/{postId}/like")
    public String likePost(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            groupPostService.likePost(postId, principal);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", ("Error"));
        }
        return "redirect:/groups/" + groupId;
    }

    // Create comment
    @PostMapping("/{postId}/comments")
    public String createComment(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @RequestParam String content,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            groupPostService.createComment(postId, content, principal);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error");
        }
        return "redirect:/groups/" + groupId + "/posts/" + postId;
    }

    // Delete comment
    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            groupPostService.deleteComment(commentId, principal);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error" + e.getMessage());
        }
        return "redirect:/groups/" + groupId + "/posts/" + postId;
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            groupPostService.deletePost(postId, principal);
            redirectAttributes.addFlashAttribute("successMessage", "The post has been deleted!");
            return "redirect:/groups/" + groupId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error when deleting post: " + e.getMessage());
            return "redirect:/groups/" + groupId + "/posts/" + postId;
        }
    }

}
package com.healthyForum.controller.posts;

import com.healthyForum.model.Post;
import com.healthyForum.model.Report;
import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.repository.PostRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.PostService;
import com.healthyForum.service.ReportService;
import com.healthyForum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final ReportService reportService;

    private final UserAccountRepository userAccountRepository;

    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserRepository userRepository, PostRepository postRepository, ReportService reportService, UserAccountRepository userAccountRepository, UserService userService) {
        this.postService = postService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reportService = reportService;
        this.userAccountRepository = userAccountRepository;
        this.userService = userService;
    }

    /**
     * List all public, non-banned posts
     */
    @GetMapping
    public String listPublicPosts(Model model) {
        model.addAttribute("posts", postService.getAllVisiblePublicPosts());
        return "posts/post_list";
    }

    /**
     * List posts created by current user
     */
    @GetMapping("/my-post")
    public String listMyPosts(Model model, Principal principal) {
        model.addAttribute("posts", postService.getPostsByCurrentUser(principal));
        return "posts/my_post";
    }

    //drafts
    @GetMapping("/drafts")
    public String showUserDrafts(Model model, Principal principal) {
        User currentUser = userService.getCurrentUser(principal);
        if (currentUser == null) {
            System.out.println("[WARN] showUserDrafts: Unauthenticated access attempt, redirecting to login.");
            return "redirect:/login";
        }
        List<Post> drafts = postService.getDrafts(currentUser.getId());
        model.addAttribute("drafts", drafts);
        return "posts/drafts";
    }

    /**
     * Show form to create a new post
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("visibilityOptions", Visibility.values());
        return "posts/post_form";
    }

    /**
     * Save a new post
     */
    @PostMapping("/create")
    public String createPost(@ModelAttribute("post") Post post, Principal principal, RedirectAttributes redirectAttributes) {
        postService.savePost(post, principal);
        if (post.isBanned()) {
            redirectAttributes.addFlashAttribute("error", "Your post contains inappropriate language and has been banned.");
            return "redirect:/posts/my-post";
        }
        redirectAttributes.addFlashAttribute("success", "Post created successfully!");
        return "redirect:/posts";
    }

    /**
     * Show form to edit a post
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        Post post = postService.getPostById(id);
        if (post == null || !postService.isOwner(post, principal)) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access");
            return "redirect:/posts";
        }
        model.addAttribute("post", post);
        model.addAttribute("visibilityOptions", Visibility.values());
        return "posts/post_form";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post updatedPost, Principal principal, RedirectAttributes redirectAttributes) {
        Post existingPost = postService.getPostById(id);
        if (existingPost == null || !postService.isOwner(existingPost, principal)) {
            redirectAttributes.addFlashAttribute("error", "You do not have permission to update this post.");
            return "redirect:/posts";
        }

        postService.updatePost(existingPost, updatedPost);
        redirectAttributes.addFlashAttribute("success", updatedPost.isDraft() ? "Draft updated." : "Post updated.");
        return "redirect:/posts";
    }

    @GetMapping("/{id}/draft-edit")
    public String showEditDraftForm(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        Post post = postService.getPostById(id);
        if (post == null || !postService.isOwner(post, principal)) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access");
            return "redirect:/posts/drafts";
        }
        model.addAttribute("post", post);
        model.addAttribute("visibilityOptions", Visibility.values());
        return "posts/post_form";
    }

    @PostMapping("/{id}/draft-edit")
    public String updateDraftForm(@PathVariable Long id, @ModelAttribute("post") Post updatedPost, Principal principal, RedirectAttributes redirectAttributes) {
        Post existingPost = postService.getPostById(id);
        if (existingPost == null || !postService.isOwner(existingPost, principal)) {
            redirectAttributes.addFlashAttribute("error", "You do not have permission to update this post.");
            return "redirect:/posts";
        }

        postService.updatePost(existingPost, updatedPost);
        redirectAttributes.addFlashAttribute("success", updatedPost.isDraft() ? "Draft updated." : "Post updated.");
        return "redirect:/posts/drafts";
    }

    /**
     * Delete a post
     */
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        boolean deleted = postService.deletePost(id, principal);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Post deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to delete this post.");
        }
        return "redirect:/posts";
    }

    @PostMapping("/{id}/report")
    public String reportPost(@PathVariable Long id, @RequestParam String reason,
                             @AuthenticationPrincipal Object authPrincipal,
                             RedirectAttributes redirectAttributes) {
        Post post = postService.getPostById(id);
        User currentUser = userService.getCurrentUser(authPrincipal);
        
        if (currentUser == null) {
            return "redirect:/login";
        }

        // The user who created the post
        User reportedUser = post.getUser();

        // Don't allow users to report their own posts
        if (reportedUser.getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không thể báo cáo bài viết của chính mình");
            return "redirect:/posts/" + id;
        }

        reportService.createPostReport(currentUser, reportedUser, post, reason);
        redirectAttributes.addFlashAttribute("success", "Báo cáo đã được gửi và sẽ được xem xét");
        return "redirect:/posts/" + id;
    }

    @GetMapping("/my-reports")
    public String getMyReports(Model model, @AuthenticationPrincipal Object authPrincipal) {
        User currentUser = userService.getCurrentUser(authPrincipal);
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Report> reports = reportService.getReportsByReporter(currentUser);
        model.addAttribute("reports", reports);
        return "post/my-reports";
    }

    /**
     * View details of a post
     */
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        Post post = postService.getPostById(id);
        if (post == null || post.isBanned() || post.isDraft()) {
            redirectAttributes.addFlashAttribute("error", "Post not available");
            return "redirect:/posts";
        }

        boolean isOwner = postService.isOwner(post, principal);

        model.addAttribute("post", post);
        model.addAttribute("isOwner", isOwner);
        return "posts/post_detail";
    }
}

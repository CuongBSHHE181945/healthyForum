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

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserAccountRepository userAccountRepository;

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
        User currentUser = getCurrentUser(principal);
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
                             @AuthenticationPrincipal Object principal,
                             RedirectAttributes redirectAttributes) {
        Post post = postService.getPostById(id);
        User currentUser = getCurrentUser(principal);
        
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
    public String getMyReports(Model model, @AuthenticationPrincipal Object principal) {
        User currentUser = getCurrentUser(principal);
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

    /**
     * Helper method to get current user from either UserDetails or OAuth2User
     */
    private User getCurrentUser(Object principal) {
        if (principal instanceof UserDetails userDetails) {
            return getCurrentUser(userDetails);
        } else if (principal instanceof OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            if (email != null) {
                return userRepository.findByEmail(email).orElse(null);
            }
            String googleId = oauth2User.getName();
            if (googleId != null) {
                UserAccount account = userAccountRepository.findByGoogleId(googleId).orElse(null);
                if (account != null) return account.getUser();
            }
        } else if (principal instanceof Principal p) {
            String principalName = p.getName();
            UserAccount account = userAccountRepository.findByUsername(principalName)
                    .orElseGet(() -> userAccountRepository.findByGoogleId(principalName).orElse(null));
            if (account != null) {
                return account.getUser();
            }
            User user = userRepository.findByEmail(principalName).orElse(null);
            if (user != null) {
                return user;
            }
        }
        return null;
    }

    private User getCurrentUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        
        // Try to find by username first using UserAccountRepository
        UserAccount account = userAccountRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (account != null) {
            return account.getUser();
        }

        // Try to find by email
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }
}

package com.healthyForum.controller.posts;

import com.healthyForum.model.Post.Comment;
import com.healthyForum.model.Post.Post;
import com.healthyForum.model.Post.PostReaction;
import com.healthyForum.model.Report;
import com.healthyForum.model.User;
import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.repository.Post.CommentRepository;
import com.healthyForum.repository.Post.PostReactionRepository;
import com.healthyForum.repository.Post.PostRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.post.CommentService;
import com.healthyForum.service.post.FileStorageService;
import com.healthyForum.service.post.PostService;
import com.healthyForum.service.ReportService;
import com.healthyForum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final ReportService reportService;

    private final UserAccountRepository userAccountRepository;

    private final UserService userService;

    private final PostReactionRepository postReactionRepository;

    private final CommentRepository commentRepository;

    private final FileStorageService fileStorageService;

    private final CommentService commentService;

    private final String uploadDir = "C:/Users/admin/Downloads/healthyForum/healthyForum/healthyForum/Uploads/";

    @Autowired
    public PostController(PostService postService, UserRepository userRepository, PostRepository postRepository, ReportService reportService, UserAccountRepository userAccountRepository, UserService userService, PostReactionRepository postReactionRepository, CommentRepository commentRepository, FileStorageService fileStorageService, CommentService commentService) {
        this.postService = postService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reportService = reportService;
        this.userAccountRepository = userAccountRepository;
        this.userService = userService;
        this.postReactionRepository = postReactionRepository;
        this.commentRepository = commentRepository;
        this.fileStorageService = fileStorageService;
        this.commentService = commentService;
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
    public String createPost(@ModelAttribute("post") Post post,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             @RequestParam("videoFile") MultipartFile videoFile,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            String uploadDir = "C:/Users/admin/Downloads/healthyForum/healthyForum/healthyForum/Uploads/";
            new File(uploadDir).mkdirs();

            if (!imageFile.isEmpty()) {
                String imageName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                imageFile.transferTo(Paths.get(uploadDir + imageName));
                post.setImageUrl("/uploads/" + imageName);
            }

            if (!videoFile.isEmpty()) {
                String videoName = UUID.randomUUID() + "_" + videoFile.getOriginalFilename();
                videoFile.transferTo(Paths.get(uploadDir + videoName));
                post.setVideoUrl("/uploads/" + videoName);
            }

            postService.savePost(post, principal);

            if (post.isBanned()) {
                redirectAttributes.addFlashAttribute("error", "Your post contains inappropriate language and has been banned.");
                return "redirect:/posts/my-post";
            }

            redirectAttributes.addFlashAttribute("success", "Post created successfully!");
            return "redirect:/posts";

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "File upload failed: " + e.getMessage());
            return "redirect:/posts/create";
        }
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
        redirectAttributes.addFlashAttribute("success", updatedPost.getVisibility() == Visibility.DRAFTS ? "Draft updated." : "Post updated.");
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
        redirectAttributes.addFlashAttribute("success",
                updatedPost.getVisibility() == Visibility.DRAFTS ? "Draft updated." : "Post updated.");
        return "redirect:/posts/drafts";
    }

    /**
     * Delete a post
     */
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, @RequestParam(required = false) String redirectUrl, Principal principal, RedirectAttributes redirectAttributes) {
        boolean deleted = postService.deletePost(id, principal);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Post deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to delete this post.");
        }
        return "redirect:" + (redirectUrl != null && !redirectUrl.isBlank() ? redirectUrl : "/posts");
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
    public String viewPost(@PathVariable Long id,
                           @RequestParam(defaultValue = "0") int page,
                           Model model,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {

        Post post = postService.getPostById(id);
        if (post == null || post.isBanned() || post.getVisibility() == Visibility.DRAFTS) {
            redirectAttributes.addFlashAttribute("error", "Post not available");
            return "redirect:/posts";
        }

        User viewer = userService.getCurrentUser(principal);
        if (!postService.isPostAccessibleByUser(post, viewer)) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to view this post.");
            return "redirect:/posts";
        }

        long likeCount = postReactionRepository.countByPostAndLiked(post, true);
        long dislikeCount = postReactionRepository.countByPostAndLiked(post, false);

        Optional<PostReaction> userReaction = postReactionRepository.findByUserAndPost(viewer, post);
        boolean likedByUser = userReaction.map(PostReaction::isLiked).orElse(false);
        boolean dislikedByUser = userReaction.map(r -> !r.isLiked()).orElse(false);

        // Phân trang bình luận với PAGE_SIZE = 10
        Page<Comment> commentPage = commentService.getCommentsByPostId(post.getId(), page, 10);

        model.addAttribute("post", post);
        model.addAttribute("isOwner", post.getUser().getId().equals(viewer.getId()));
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("dislikeCount", dislikeCount);
        model.addAttribute("likedByUser", likedByUser);
        model.addAttribute("dislikedByUser", dislikedByUser);

        model.addAttribute("commentPage", commentPage);
        model.addAttribute("currentPage", page);

        return "posts/post_detail";
    }



    @PostMapping("/{id}/like")
    public String likePost(@PathVariable Long id, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Post post = postService.getPostById(id);

        if (user != null && post != null) {
            PostReaction reaction = postReactionRepository.findByUserAndPost(user, post).orElse(null);

            if (reaction == null) {
                reaction = new PostReaction();
                reaction.setUser(user);
                reaction.setPost(post);
                reaction.setLiked(true);
                postReactionRepository.save(reaction);
            } else if (!reaction.isLiked()) {
                reaction.setLiked(true);
                postReactionRepository.save(reaction);
            }
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/dislike")
    public String dislikePost(@PathVariable Long id, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Post post = postService.getPostById(id);

        if (user != null && post != null) {
            PostReaction reactions = postReactionRepository.findByUserAndPost(user, post).orElse(null);

            if (reactions == null) {
                reactions = new PostReaction();
                reactions.setUser(user);
                reactions.setPost(post);
                reactions.setLiked(false);
                postReactionRepository.save(reactions);
            } else if (reactions.isLiked()) {
                reactions.setLiked(false);
                postReactionRepository.save(reactions);
            }
        }
        return "redirect:/posts/" + id;
    }


    @PostMapping("/{id}/comment")
    public String postComment(@PathVariable Long id,
                              @RequestParam String content,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để bình luận.");
            return "redirect:/login";
        }

        Post post = postService.getPostById(id);
        if (post == null) {
            redirectAttributes.addFlashAttribute("error", "Bài viết không tồn tại.");
            return "redirect:/posts";
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());

        commentService.saveComment(comment);
        redirectAttributes.addFlashAttribute("success", "Bình luận đã được gửi.");

        return "redirect:/posts/" + id;
    }


}

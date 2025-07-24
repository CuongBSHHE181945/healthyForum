package com.healthyForum.controller.posts;

import com.healthyForum.model.Enum.ReactionType;
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
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("mode", "create");
        return "posts/post_form";
    }

    /**
     * Save a new post
     */
    @PostMapping("/create")
    public String createPost(@ModelAttribute("post") Post post,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

            if (!imageFile.isEmpty()) {
                post.setImageUrl(fileStorageService.save(imageFile));
            }



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
        model.addAttribute("mode", "edit");
        return "posts/post_form";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id, @RequestParam("imageFile") MultipartFile imageFile, @ModelAttribute("post") Post updatedPost, Principal principal, RedirectAttributes redirectAttributes) {
        Post existingPost = postService.getPostById(id);
        if (imageFile != null && !imageFile.isEmpty()) {
            String newImageUrl = fileStorageService.save(imageFile);
            updatedPost.setImageUrl(newImageUrl);
        } else {
            updatedPost.setImageUrl(existingPost.getImageUrl());
        }

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
        model.addAttribute("mode", "drafts");
        return "posts/post_form";
    }

    @PostMapping("/{id}/draft-edit")
    public String updateDraftForm(@PathVariable Long id, @RequestParam("imageFile") MultipartFile imageFile, @ModelAttribute("post") Post updatedPost, Principal principal, RedirectAttributes redirectAttributes) {
        Post existingPost = postService.getPostById(id);

        if (imageFile != null && !imageFile.isEmpty()) {
            String newImageUrl = fileStorageService.save(imageFile);
            updatedPost.setImageUrl(newImageUrl);
        } else {
            updatedPost.setImageUrl(existingPost.getImageUrl());
        }

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

        long likeCount = postReactionRepository.countByPostAndType(post, ReactionType.LIKE);
        long dislikeCount = postReactionRepository.countByPostAndType(post, ReactionType.DISLIKE);

        Optional<PostReaction> userReaction = postReactionRepository.findByUserAndPost(viewer, post);
        boolean likedByUser = userReaction.map(r -> r.getType() == ReactionType.LIKE).orElse(false);
        boolean dislikedByUser = userReaction.map(r -> r.getType() == ReactionType.DISLIKE).orElse(false);

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
        model.addAttribute("editingCommentIds", Set.of());
        model.addAttribute("currentUserId", viewer.getId());


        return "posts/post_detail";
    }



    @PostMapping("/{id}/like")
    public String likePost(@PathVariable Long id, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Post post = postService.getPostById(id);

        if (user != null && post != null) {
            Optional<PostReaction> optional = postReactionRepository.findByUserAndPost(user, post);
            if (optional.isPresent()) {
                PostReaction existing = optional.get();
                if (existing.getType() == ReactionType.LIKE) {
                    postReactionRepository.delete(existing); // Toggle off
                } else {
                    existing.setType(ReactionType.LIKE);
                    postReactionRepository.save(existing); // Switch from DISLIKE
                }
            } else {
                PostReaction newReaction = new PostReaction();
                newReaction.setUser(user);
                newReaction.setPost(post);
                newReaction.setType(ReactionType.LIKE);
                postReactionRepository.save(newReaction);
            }
        }

        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/dislike")
    public String dislikePost(@PathVariable Long id, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Post post = postService.getPostById(id);

        if (user != null && post != null) {
            Optional<PostReaction> optional = postReactionRepository.findByUserAndPost(user, post);
            if (optional.isPresent()) {
                PostReaction existing = optional.get();
                if (existing.getType() == ReactionType.DISLIKE) {
                    postReactionRepository.delete(existing); // Toggle off
                } else {
                    existing.setType(ReactionType.DISLIKE);
                    postReactionRepository.save(existing); // Switch from LIKE
                }
            } else {
                PostReaction newReaction = new PostReaction();
                newReaction.setUser(user);
                newReaction.setPost(post);
                newReaction.setType(ReactionType.DISLIKE);
                postReactionRepository.save(newReaction);
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

    @GetMapping("/{postId}/comments/{commentId}/edit")
    public String showEditCommentForm(@PathVariable Long postId,
                                      @PathVariable Long commentId,
                                      Principal principal,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        User user = userService.getCurrentUser(principal);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để chỉnh sửa bình luận.");
            return "redirect:/login";
        }

        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (optionalComment.isEmpty() || !optionalComment.get().getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền chỉnh sửa bình luận này.");
            return "redirect:/posts/" + postId;
        }

        // Lấy lại bài viết để hiển thị chi tiết
        Post post = postService.getPostById(postId);
        Page<Comment> commentPage = commentService.getCommentsByPostId(postId, 0, 10);

        model.addAttribute("post", post);
        model.addAttribute("commentPage", commentPage);
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("editingCommentIds", Set.of(commentId));


        return "posts/post_detail";

    }


    @PostMapping("/comments/{commentId}/edit")
    public String updateComment(@PathVariable("commentId") Long commentId,
                                @RequestParam("content") String content,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để chỉnh sửa bình luận.");
            return "redirect:/login";
        }

        try {
            String username = principal.getName();
            commentService.updateComment(commentId, content, username);
            redirectAttributes.addFlashAttribute("success", "Bình luận đã được cập nhật.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // Redirect về bài viết gốc sau khi sửa comment
        Long postId = commentService.getPostIdByCommentId(commentId);
        return "redirect:/posts/" + postId;
    }

}

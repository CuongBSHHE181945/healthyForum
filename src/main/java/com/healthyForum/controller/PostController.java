//package com.healthyForum.controller;
//
//import com.healthyForum.model.Post;
//import com.healthyForum.model.Report;
//import com.healthyForum.model.User;
//import com.healthyForum.service.PostService;
//import com.healthyForum.service.ReportService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import jakarta.validation.Valid;
//import com.healthyForum.repository.UserRepository;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/post")
//public class PostController {
//
//    @Autowired
//    private PostService postService;
//
//    @Autowired
//    private ReportService reportService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    // Display all public posts
//    @GetMapping
//    public String getAllPosts(Model model) {
//        model.addAttribute("posts", postService.getPublicPosts());
//        return "post/post_list";
//    }
//
//    // Display a specific post
//    @GetMapping("/{id}")
//    public String getPost(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
//        Post post = postService.getPostById(id);
//        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
//
//        // Check if user can access this post
//        if (currentUser != null && postService.isPostAccessibleByUser(post, currentUser)) {
//            model.addAttribute("post", post);
//            model.addAttribute("isAuthor", post.getUser().getUserID().equals(currentUser.getUserID()));
//            return "post/post_view"; // This is the detail view, not the form
//        } else {
//            return "redirect:/post?error=Post not accessible";
//        }
//    }
//
//    // Display form to create a new post
//    @GetMapping("/create")
//    public String createPostForm(Model model) {
//        model.addAttribute("post", new Post());
//        model.addAttribute("visibilityOptions", Post.Visibility.values());
//        return "post/post_form";
//    }
//
//    // Process post creation
//    @PostMapping("/create")
//    public String createPost(@Valid @ModelAttribute Post post, BindingResult result,
//                             @AuthenticationPrincipal UserDetails userDetails,
//                             RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            return "post/post_form";
//        }
//
//        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
//        if (currentUser == null) {
//            return "redirect:/login";
//        }
//
//        post.setUser(currentUser);
//        Post savedPost = postService.createPost(post);
//
//        redirectAttributes.addFlashAttribute("success", "Post created successfully");
//        return "redirect:/post/" + savedPost.getId();
//    }
//
//    // Display form to edit a post
//    @GetMapping("/{id}/edit")
//    public String editPostForm(@PathVariable Long id, Model model,
//                               @AuthenticationPrincipal UserDetails userDetails,
//                               RedirectAttributes redirectAttributes) {
//        Post post = postService.getPostById(id);
//        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
//
//        // Check if user is the author
//        if (currentUser != null && post.getUser().getUserID().equals(currentUser.getUserID())) {
//            model.addAttribute("post", post);
//            model.addAttribute("visibilityOptions", Post.Visibility.values());
//            return "post/post_edit";
//        } else {
//            redirectAttributes.addFlashAttribute("error", "You can only edit your own posts");
//            return "redirect:/post";
//        }
//    }
//
//    // Process post update
//    @PostMapping("/{id}/edit")
//    public String updatePost(@PathVariable Long id, @Valid @ModelAttribute Post post,
//                             BindingResult result, @AuthenticationPrincipal UserDetails userDetails,
//                             RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            return "post/post_edit";
//        }
//
//        Post existingPost = postService.getPostById(id);
//        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
//
//        // Check if user is the author
//        if (currentUser != null && existingPost.getUser().getUserID().equals(currentUser.getUserID())) {
//            // Update only allowed fields
//            existingPost.setTitle(post.getTitle());
//            existingPost.setContent(post.getContent());
//            existingPost.setVisibility(post.getVisibility());
//            existingPost.setDraft(post.isDraft());
//
//            postService.updatePost(existingPost);
//            redirectAttributes.addFlashAttribute("success", "Post updated successfully");
//            return "redirect:/post/" + id;
//        } else {
//            redirectAttributes.addFlashAttribute("error", "You can only edit your own posts");
//            return "redirect:/post";
//        }
//    }
//
//    // Delete a post
//    @PostMapping("/{id}/delete")
//    public String deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails,
//                             RedirectAttributes redirectAttributes) {
//        Post post = postService.getPostById(id);
//        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
//
//        // Check if user is the author
//        if (currentUser != null && post.getUser().getUserID().equals(currentUser.getUserID())) {
//            postService.deletePost(id);
//            redirectAttributes.addFlashAttribute("success", "Post deleted successfully");
//            return "redirect:/post";
//        } else {
//            redirectAttributes.addFlashAttribute("error", "You can only delete your own posts");
//            return "redirect:/post";
//        }
//    }
//
//    // View user's own posts (including drafts)
//    @GetMapping("/my-posts")
//    public String getMyPosts(Model model, @AuthenticationPrincipal UserDetails userDetails) {
//        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
//        if (currentUser == null) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("posts", postService.getPostsByUser(currentUser));
//        return "post/my-posts";
//    }
//
//    // View user's drafts
//    @GetMapping("/drafts")
//    public String getDrafts(Model model, @AuthenticationPrincipal UserDetails userDetails) {
//        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
//        if (currentUser == null) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("drafts", postService.getUserDrafts(currentUser));
//        return "post/drafts";
//    }
//
//    // Search posts
//    @GetMapping("/search")
//    public String searchPosts(@RequestParam String query, Model model) {
//        model.addAttribute("posts", postService.searchPostsByTitle(query));
//        model.addAttribute("query", query);
//        return "post/search-results"; // Changed from posts/ to post/
//    }
//
//    // Add to PostController.java
//    @PostMapping("/{id}/report")
//    public String reportPost(@PathVariable Long id, @RequestParam String reason,
//                             @AuthenticationPrincipal UserDetails userDetails,
//                             RedirectAttributes redirectAttributes) {
//        Post post = postService.getPostById(id);
//        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
//        // The user who created the post
//        User reportedUser = post.getUser();
//
//        if (currentUser == null) {
//            return "redirect:/login";
//        }
//
//        // Don't allow users to report their own posts
//        if (reportedUser.getUserID().equals(currentUser.getUserID())) {
//            redirectAttributes.addFlashAttribute("error", "Bạn không thể báo cáo bài viết của chính mình");
//            return "redirect:/post/" + id;
//        }
//
//        reportService.createPostReport(currentUser, reportedUser, post, reason);
//        redirectAttributes.addFlashAttribute("success", "Báo cáo đã được gửi và sẽ được xem xét");
//        return "redirect:/post/" + id;
//    }
//
//    @GetMapping("/my-reports")
//    public String getMyReports(Model model, @AuthenticationPrincipal UserDetails userDetails) {
//        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
//        if (currentUser == null) {
//            return "redirect:/login";
//        }
//
//        List<Report> reports = reportService.getReportsByReporter(currentUser);
//        model.addAttribute("reports", reports);
//        return "post/my-reports";
//    }
//}
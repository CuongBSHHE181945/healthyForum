package com.healthyForum.controller;

import com.healthyForum.model.Post.Post;
import com.healthyForum.service.post.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestMapping("/")
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    private final PostService postService;

    public IndexController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String index(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       @RequestParam(value = "filter", required = false) String filter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("[IndexController] index() called. Authenticated: {}", authentication != null && authentication.isAuthenticated());
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            try {
                Pageable pageable = PageRequest.of(page, size);
                Page<Post> postPage;
                if ("trending".equalsIgnoreCase(filter)) {
                    postPage = postService.getTrendingPosts(pageable);
                } else if ("following".equalsIgnoreCase(filter)) {
                    postPage = postService.getFollowingPosts(pageable, authentication.getName());
                } else {
                    postPage = postService.getAllVisiblePublicPosts(pageable);
                }
                logger.info("[IndexController] Adding {} posts to model.", postPage.getContent().size());
                model.addAttribute("posts", postPage.getContent());
                model.addAttribute("likeCounts", postService.getLikeCounts(postPage.getContent()));
                model.addAttribute("dislikeCounts", postService.getDislikeCounts(postPage.getContent()));
                model.addAttribute("currentPage", postPage.getNumber());
                model.addAttribute("totalPages", postPage.getTotalPages());
                model.addAttribute("hasNext", postPage.hasNext());
                model.addAttribute("hasPrevious", postPage.hasPrevious());
                model.addAttribute("filter", filter);
            } catch (Exception e) {
                logger.error("[IndexController] Exception: ", e);
                model.addAttribute("feedError", "Unable to load feed. Please try again later");
            }
            return "homePage";
        } else {
            logger.info("[IndexController] Not authenticated, redirecting to login.");
            return "redirect:/login";
        }
    }
}

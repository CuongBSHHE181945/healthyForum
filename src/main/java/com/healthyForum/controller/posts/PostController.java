package com.healthyForum.controller.posts;

import com.healthyForum.model.Post;
import com.healthyForum.model.User;
import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.service.PostService;
import com.healthyForum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts/new")
    public String showPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "posts/post_form";
    }

    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post, Principal principal) {
        postService.savePost(post, principal.getName());
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "posts/post_list";
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        return "posts/post_detail";
    }

    @GetMapping("/posts/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model, Principal principal) {
        if (!postService.isOwner(id, principal.getName())) {
            return "redirect:/posts";
        }
        model.addAttribute("post", postService.getPostById(id));
        return "posts/post_form";
    }

    @PostMapping("/posts/{id}/update")
    public String updatePost(@ModelAttribute Post post, Principal principal) {
        if (!postService.isOwner(post.getId(), principal.getName())) {
            return "redirect:/posts";
        }
        postService.savePost(post, principal.getName());
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, Principal principal) {
        if (!postService.isOwner(id, principal.getName())) {
            return "redirect:/posts";
        }
        postService.deletePost(id);
        return "redirect:/posts";
    }


}

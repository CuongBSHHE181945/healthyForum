package com.healthyForum.service;

import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.model.Post;
import com.healthyForum.model.User;
import com.healthyForum.repository.PostRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.repository.keywordFiltering.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserService userService;

    // Save a post (new or edited) with user from Principal
    public void savePost(Post post, Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        post.setUser(user);
        filterAndBanIfNeeded(post);
        postRepository.save(post);
    }

    // Get all posts (admin only)
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Get one post by ID
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    // Delete post if current user is owner
    public boolean deletePost(Long id, Principal principal) {
        Post post = getPostById(id);
        if (post != null && isOwner(post, principal)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Check if current user owns the post
    public boolean isOwner(Post post, Principal principal) {
        if (post == null) return false;
        User currentUser = userService.getCurrentUser(principal);
        return currentUser != null && post.getUser().getId().equals(currentUser.getId());
    }

    // List all public posts that are not banned
    public List<Post> getAllVisiblePublicPosts() {
        return postRepository.findByVisibilityAndBannedFalse(Visibility.PUBLIC);
    }

    // List all posts by current user (my posts)
    public List<Post> getPostsByCurrentUser(Principal principal) {
        User currentUser = userService.getCurrentUser(principal);
        if (currentUser == null) {
            return List.of(); // Return empty list if user not found
        }
        return postRepository.findByUserIdAndVisibilityNot(currentUser.getId(), Visibility.DRAFTS);
    }

    public List<Post> getDrafts(Long userId) {
        return postRepository.findByUserIdAndVisibility(userId, Visibility.DRAFTS);
    }

    public Post banPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + postId));

        post.setBanned(true);
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public void updatePost(Post existingPost, Post updatedPost) {
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setVisibility(updatedPost.getVisibility());
        existingPost.setImageUrl(updatedPost.getImageUrl());
        existingPost.setUpdatedAt(LocalDateTime.now()); // optional
        postRepository.save(existingPost);
    }

    public Post unbanPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + postId));
        post.setBanned(false);
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public boolean isPostAccessibleByUser(Post post, User user) {
        if (post.getVisibility() == Visibility.PUBLIC) {
            return true;
        }

        // Chủ bài viết
        if (post.getUser().getId().equals(user.getId())) {
            return true;
        }

        // Follower của chủ bài viết
        if (post.getVisibility() == Visibility.FOLLOWERS_ONLY) {
            return userService.isFollowing(user, post.getUser());
        }

        return false;
    }

    private boolean containsBannedKeyword(String text) {
        if (text == null) return false;
        String lower = text.toLowerCase();
        return keywordRepository.findAll().stream()
                .anyMatch(k -> lower.contains(k.getWord().toLowerCase()));
    }

    public boolean filterAndBanIfNeeded(Post post) {
        if (containsBannedKeyword(post.getTitle()) || containsBannedKeyword(post.getContent())) {
            post.setBanned(true);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
            return true;
        }
        return false;
    }
}



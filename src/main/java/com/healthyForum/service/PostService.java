package com.healthyForum.service;

import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.model.Post;
import com.healthyForum.model.User;
import com.healthyForum.repository.PostRepository;
import com.healthyForum.repository.UserRepository;
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
        private KeywordRepository keywordRepository;

        // Save a post (new or edited) with user from Principal
        public void savePost(Post post, Principal principal) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
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
            return post != null && post.getUser().getUsername().equals(principal.getName());
        }

        // List all public posts that are not banned
        public List<Post> getAllVisiblePublicPosts() {
            return postRepository.findByIsDraftFalseAndVisibilityAndBannedFalse(Visibility.PUBLIC);
        }

        // List all posts by current user (my posts)
        public List<Post> getPostsByCurrentUser(Principal principal) {
            return postRepository.findByUserUsername(principal.getName());
        }

        public List<Post> getDrafts(String username) {
            return postRepository.findByUserUsernameAndIsDraftTrue(username);
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
            existingPost.setDraft(updatedPost.isDraft());
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

            if (post.getUser().getUserID().equals(user.getUserID())) {
                return true;
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



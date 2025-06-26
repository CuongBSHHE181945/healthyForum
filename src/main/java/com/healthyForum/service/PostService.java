package com.healthyForum.service;

import com.healthyForum.model.Post;
import com.healthyForum.model.User;
import com.healthyForum.repository.PostRepository;
import com.healthyForum.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public void savePost(Post post, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        post.setUser(user);
        postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public boolean isOwner(Long postId, String username) {
        Post post = getPostById(postId);
        return post != null && post.getUser().getUsername().equals(username);
    }

    @Transactional
    public Post banPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + postId));
        post.setBanned(true);
        post.setUpdatedAt(new Date());
        return postRepository.save(post);
    }

    @Transactional
    public Post unbanPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + postId));
        post.setBanned(false);
        post.setUpdatedAt(new Date());
        return postRepository.save(post);
    }
}


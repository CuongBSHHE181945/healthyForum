package com.healthyForum.service;

import com.healthyForum.model.Post;
import com.healthyForum.model.User;
import com.healthyForum.repository.PostRepository;
import com.healthyForum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}


package com.healthyForum.service;

import com.healthyForum.model.Post.Post;
import com.healthyForum.model.User;
import com.healthyForum.repository.Post.PostRepository;
import com.healthyForum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.healthyForum.model.Enum.Visibility;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public SearchServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> search(String query) {
        Map<String, Object> results = new HashMap<>();
        // Search posts
        String pattern = "%" + query.toLowerCase() + "%";
        logger.info("[SearchService] Searching posts and users for query: '{}' (pattern: '{}')", query, pattern);
        Page<Post> postPage = postRepository.searchPosts(
                pattern,
                Visibility.PUBLIC,
                PageRequest.of(0, 5)
        );
        List<Post> postEntities = postPage.getContent();
        logger.info("[SearchService] Found {} post(s) for query: '{}'", postEntities.size(), query);
        List<Map<String, Object>> posts = postEntities.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("snippet", post.getContent().length() > 100 ? post.getContent().substring(0, 100) + "..." : post.getContent());
            map.put("userDisplayName", post.getUser().getDisplayName());
            map.put("createdAt", post.getCreatedAt());
            map.put("link", "/posts/" + post.getId());
            return map;
        }).toList();

        // Search users
        List<User> userEntities = userRepository.searchUsersByFullname(
                query,
                PageRequest.of(0, 5)
        );
        logger.info("[SearchService] Found {} user(s) for query: '{}'", userEntities.size(), query);
        List<Map<String, Object>> users = userEntities.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("displayName", user.getDisplayName());
            map.put("username", user.getUsername());
            map.put("avatar", null); // Add avatar if available in your model
            map.put("link", "/profile/" + user.getId());
            return map;
        }).toList();

        results.put("posts", posts);
        results.put("users", users);
        return results;
    }
} 
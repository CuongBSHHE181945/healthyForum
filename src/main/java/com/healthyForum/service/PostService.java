package com.healthyForum.service;

import com.healthyForum.model.Post;
import com.healthyForum.model.User;
import com.healthyForum.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class PostService{

    @Autowired
    private PostRepository postRepository;

    @Transactional
    public Post createPost(Post post) {
        // Set creation and update timestamps
        Date now = new Date();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @Transactional
    public Post updatePost(Post post) {
        // Verify post exists
        postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + post.getId()));

        // Update the timestamp
        post.setUpdatedAt(new Date());
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user);
    }



    @Transactional(readOnly = true)
    public List<Post> getPostsByVisibility(Post.Visibility visibility) {
        return postRepository.findByVisibility(visibility);
    }

    @Transactional(readOnly = true)
    public List<Post> searchPostsByTitle(String searchTerm) {
        return postRepository.findByTitleContainingIgnoreCaseAndIsDraftFalse(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<Post> searchPostsByContent(String searchTerm) {
        return postRepository.findByContentContainingIgnoreCaseAndIsDraftFalse(searchTerm);
    }


    @Transactional(readOnly = true)
    public List<Post> getUserDrafts(User user) {
        return postRepository.findByUserAndIsDraftTrue(user);
    }


    @Transactional(readOnly = true)
    public List<Post> getRecentPublicPosts() {
        return postRepository.findTop10ByVisibilityAndIsDraftFalseOrderByCreatedAtDesc(Post.Visibility.PUBLIC);
    }

    @Transactional(readOnly = true)
    public boolean isPostAccessibleByUser(Post post, User user) {
        // Post is accessible if:
        // 1. It's a public post
        // 2. The user is the author
        // 3. It's a FOLLOWERS post (future implementation would check follower relationship)

        if (post.getVisibility() == Post.Visibility.PUBLIC) {
            return true;
        }

        if (post.getUser().getUserID().equals(user.getUserID())) {
            return true;
        }

        // For FOLLOWERS visibility, we'd need to check if the user follows the post author
        // This would require a follow/follower relationship implementation

        return false;
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

    @Transactional(readOnly = true)
    public List<Post> getPublicPosts() {
        return postRepository.findByVisibilityAndIsDraftFalseAndBannedFalseOrderByCreatedAtDesc(Post.Visibility.PUBLIC);
    }


}
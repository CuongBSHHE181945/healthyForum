package com.healthyForum.service;

import com.healthyForum.model.Blog;
import com.healthyForum.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public void suspendBlog(Long id) {
        Blog blog = blogRepository.findById(id).orElse(null);
        if (blog != null) {
            blog.setSuspended(true);
            blogRepository.save(blog);
        }
    }

    public void unsuspendBlog(Long id) {
        Blog blog = blogRepository.findById(id).orElse(null);
        if (blog != null) {
            blog.setSuspended(false);
            blogRepository.save(blog);
        }
    }
}

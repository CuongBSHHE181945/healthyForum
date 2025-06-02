package com.healthyForum.repository;

import com.healthyForum.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BlogRepository extends JpaRepository<Blog, Long> {}

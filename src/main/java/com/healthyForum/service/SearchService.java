package com.healthyForum.service;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {
    Map<String, Object> search(String query);
} 
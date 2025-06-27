package com.healthyForum.service.keywordFiltering;


import com.healthyForum.model.keywordFiltering.BannedKeyword;
import com.healthyForum.repository.keywordFiltering.BannedKeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ContentFilterService {

    @Autowired
    private BannedKeywordRepository bannedKeywordRepository;

    private Map<String, String> keywordCache = new HashMap<>();
    private long lastCacheUpdate = 0;
    private static final long CACHE_TIMEOUT = 600000; // 10 minutes

    public String filterContent(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        ensureCacheIsUpToDate();

        String filteredContent = content;
        for (Map.Entry<String, String> entry : keywordCache.entrySet()) {
            String keyword = entry.getKey();
            String replacement = entry.getValue();

            // Create a pattern that matches the word boundary
            String pattern = "\\b" + Pattern.quote(keyword) + "\\b";
            filteredContent = filteredContent.replaceAll("(?i)" + pattern, replacement);
        }

        return filteredContent;
    }

    @Transactional
    public BannedKeyword addKeyword(BannedKeyword keyword) {
        keyword.setCreatedAt(new java.util.Date());
        keyword.setUpdatedAt(new java.util.Date());
        BannedKeyword saved = bannedKeywordRepository.save(keyword);
        refreshCache();
        return saved;
    }

    @Transactional(readOnly = true)
    public List<BannedKeyword> getAllKeywords() {
        return bannedKeywordRepository.findAll();
    }

    private void ensureCacheIsUpToDate() {
        long now = System.currentTimeMillis();
        if (keywordCache.isEmpty() || now - lastCacheUpdate > CACHE_TIMEOUT) {
            refreshCache();
        }
    }

    private void refreshCache() {
        keywordCache.clear();
        List<BannedKeyword> activeKeywords = bannedKeywordRepository.findByActiveTrue();

        for (BannedKeyword keyword : activeKeywords) {
            String replacement = keyword.getReplacement();
            if (replacement == null || replacement.isEmpty()) {
                // Default replacement is asterisks
                replacement = "*".repeat(keyword.getWord().length());
            }
            keywordCache.put(keyword.getWord(), replacement);
        }

        lastCacheUpdate = System.currentTimeMillis();
    }
}

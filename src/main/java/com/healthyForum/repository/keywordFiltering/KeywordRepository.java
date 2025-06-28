package com.healthyForum.repository.keywordFiltering;


import com.healthyForum.model.keywordFiltering.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}

package com.healthyForum.repository.keywordFiltering;


import com.healthyForum.model.keywordFiltering.BannedKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BannedKeywordRepository extends JpaRepository<BannedKeyword, Long> {
    List<BannedKeyword> findByActiveTrue();
}

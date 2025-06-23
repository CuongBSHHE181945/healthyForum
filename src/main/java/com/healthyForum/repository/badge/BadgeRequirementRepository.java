package com.healthyForum.repository.badge;

import com.healthyForum.model.badge.BadgeRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRequirementRepository extends JpaRepository<BadgeRequirement, Long> {
    List<BadgeRequirement> findByBadgeId(int badgeId);
    List<BadgeRequirement> findBySourceTypeNameAndSourceId(String sourceTypeName, int challengeId);
}
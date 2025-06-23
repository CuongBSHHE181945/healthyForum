package com.healthyForum.repository.badge;

import com.healthyForum.model.badge.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository<UserBadgeId> extends JpaRepository<UserBadge, UserBadgeId> {
    List<UserBadge> findByIdUserId(Long userId);
    boolean existsByIdUserIdAndIdBadgeId(Long userId, int badgeId);
    Optional<UserBadge> findByIdUserIdAndIdBadgeId(Long userId, int badgeId);
    //    Spring Data requires the full path:
    //    id.userId → idUserId
    //    id.badgeId → idBadgeId
}

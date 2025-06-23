package com.healthyForum.service.badge;

import com.healthyForum.model.badge.Badge;
import com.healthyForum.model.badge.UserBadge;
import com.healthyForum.model.badge.UserBadgeId;
import com.healthyForum.repository.badge.UserBadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;

    public UserBadgeService(UserBadgeRepository userBadgeRepository) {
        this.userBadgeRepository = userBadgeRepository;
    }

    public List<UserBadge> getAllUnlockedByUser(Long userId) {
        return userBadgeRepository.findByIdUserId(userId);
    }

    public void setBadgeDisplayed(Long userId, int badgeId) {
        Optional<UserBadge> userBadgeOpt = userBadgeRepository.findByIdUserIdAndIdBadgeId(userId, badgeId);

        UserBadge userBadge = userBadgeOpt.get();
        userBadge.setDisplayed(true);
        userBadgeRepository.save(userBadge);
    }

    public Optional<UserBadge> findByUserIdAndBadgeId(Long userId, int badgeId){
        return userBadgeRepository.findByIdUserIdAndIdBadgeId(userId, badgeId);
    }

    public void updateDisplayedBadges(Long userId, List<Integer> displayedBadgeIds) {
        if (displayedBadgeIds == null) {
            displayedBadgeIds = List.of(); // or Collections.emptyList();
        }

        List<UserBadge> userBadges = userBadgeRepository.findByIdUserId(userId);

        for (UserBadge badge : userBadges) {
            boolean shouldDisplay = displayedBadgeIds.contains(badge.getBadge().getId());
            badge.setDisplayed(shouldDisplay);
        }

        userBadgeRepository.saveAll(userBadges);
    }
}

package com.healthyForum.service.badge;

import com.healthyForum.model.badge.Badge;
import com.healthyForum.model.badge.BadgeRequirement;
import com.healthyForum.model.badge.UserBadge;
import com.healthyForum.model.badge.UserBadgeId;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.badge.BadgeRepository;
import com.healthyForum.repository.badge.BadgeRequirementRepository;
import com.healthyForum.repository.badge.UserBadgeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final BadgeRequirementRepository badgeRequirementRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;

    public BadgeService(BadgeRepository badgeRepository, BadgeRequirementRepository badgeRequirementRepo, UserBadgeRepository userBadgeRepository, UserRepository userRepository) {
        this.badgeRepository = badgeRepository;
        this.badgeRequirementRepository = badgeRequirementRepo;
        this.userBadgeRepository = userBadgeRepository;
        this.userRepository = userRepository;
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Optional<Badge> findById(int id){
        return badgeRepository.findById(id);
    }

    public void checkAndAwardBadge(Long userId, int challengeId) {
        List<BadgeRequirement> reqs = badgeRequirementRepository.findBySourceTypeNameAndSourceId("CHALLENGE", challengeId);

        for (BadgeRequirement req : reqs) {
            boolean hasBadge = userBadgeRepository.existsByIdUserIdAndIdBadgeId(userId, req.getBadge().getId());
            if (!hasBadge) {
                UserBadge ub = new UserBadge();
                ub.setId(new UserBadgeId(userId, req.getBadge().getId()));
                ub.setUser(userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
                ub.setBadge(req.getBadge());
                ub.setEarnedAt(LocalDateTime.now());
                ub.setDisplayed(false);
                userBadgeRepository.save(ub);
            }
        }
    }
}

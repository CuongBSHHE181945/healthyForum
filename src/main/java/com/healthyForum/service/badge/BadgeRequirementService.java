package com.healthyForum.service.badge;

import com.healthyForum.model.badge.BadgeRequirement;
import com.healthyForum.repository.badge.BadgeRepository;
import com.healthyForum.repository.badge.BadgeRequirementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BadgeRequirementService {
    private final BadgeRequirementRepository badgeRequirementRepository;

    public BadgeRequirementService(BadgeRequirementRepository badgeRequirementRepository) {
        this.badgeRequirementRepository = badgeRequirementRepository;
    }

    public List<BadgeRequirement> getRequirementsForBadge(int badgeId){
        return badgeRequirementRepository.findByBadgeId(badgeId);
    }
}

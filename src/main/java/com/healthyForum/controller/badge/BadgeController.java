package com.healthyForum.controller.badge;

import com.healthyForum.model.User;
import com.healthyForum.model.badge.Badge;
import com.healthyForum.model.badge.BadgeRequirement;
import com.healthyForum.model.badge.UserBadge;
import com.healthyForum.model.challenge.Challenge;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.badge.BadgeRepository;
import com.healthyForum.service.badge.BadgeRequirementService;
import com.healthyForum.service.badge.BadgeService;
import com.healthyForum.service.badge.UserBadgeService;
import com.healthyForum.service.challenge.ChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/badge")
public class BadgeController {

    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;
    private final BadgeRequirementService badgeRequirementService;
    private final UserRepository userRepository;
    private final ChallengeService challengeService;

    public BadgeController(BadgeService badgeService, UserBadgeService userBadgeService, BadgeRequirementService badgeRequirementService, UserRepository userRepository, ChallengeService challengeService) {
        this.badgeService = badgeService;
        this.userBadgeService = userBadgeService;
        this.badgeRequirementService = badgeRequirementService;
        this.userRepository = userRepository;
        this.challengeService = challengeService;
    }

    @GetMapping("/{id}")
    public String viewBadgeDetail(@PathVariable("id") int badgeId, Principal principal, Model model) {
        Badge badge = badgeService.findById(badgeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Badge not found"));

        List<BadgeRequirement> requirements = badgeRequirementService.getRequirementsForBadge(badgeId);

        boolean unlocked = false;
        LocalDateTime earnedAt = null;
        User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Optional<UserBadge> userBadgeOpt = userBadgeService.findByUserIdAndBadgeId(user.getUserID(), badgeId);
            if (userBadgeOpt.isPresent()) {
                unlocked = true;
                earnedAt = userBadgeOpt.get().getEarnedAt();
            }

        List<String> challengeNames = new ArrayList<>();
        for (BadgeRequirement req : requirements) {
            String challengeName = null;
            if ("CHALLENGE".equalsIgnoreCase(req.getSourceType().getName())) {
                Challenge challenge = challengeService.getChallengeById(req.getSourceId()).orElse(null);
                if (challenge != null) {
                    challengeName = challenge.getName();
                }
            }
            challengeNames.add(challengeName);
        }

        model.addAttribute("badge", badge);
        model.addAttribute("requirements", requirements);
        model.addAttribute("challengeNames", challengeNames);
        model.addAttribute("unlocked", unlocked);
        model.addAttribute("earnedAt", earnedAt);

        return "profile/badge-detail";
    }
}


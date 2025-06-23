package com.healthyForum.controller.badge;

import com.healthyForum.model.User;
import com.healthyForum.model.badge.Badge;
import com.healthyForum.model.badge.UserBadge;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.UserService;
import com.healthyForum.service.badge.BadgeService;
import com.healthyForum.service.badge.UserBadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user-profile")
public class UserProfile {
    private final UserRepository userRepository;

    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;

    public UserProfile(UserRepository userRepository, BadgeService badgeService, UserBadgeService userBadgeService) {
        this.userRepository = userRepository;
        this.badgeService = badgeService;
        this.userBadgeService = userBadgeService;
    }

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));;

        List<UserBadge> allUserBadges = userBadgeService.getAllUnlockedByUser(user.getUserID());
        List<Badge> unlockedBadges = new ArrayList<>();
        List<Badge> lockedBadges = badgeService.getAllBadges(); // clone to remove unlocked

        List<Badge> displayedBadges = new ArrayList<>();

        for (UserBadge ub : allUserBadges) {
            Badge badge = ub.getBadge();
            unlockedBadges.add(badge);
            lockedBadges.remove(badge);
            if (ub.isDisplayed()) {
                displayedBadges.add(badge);
            }
        }

        model.addAttribute("displayedBadges", displayedBadges);
        model.addAttribute("unlockedBadges", unlockedBadges);
        model.addAttribute("lockedBadges", lockedBadges);

        return "profile/profile"; // profile.html
    }

    @PostMapping("display-badge")
    @ResponseBody
    public ResponseEntity<?> addDisplayedBadge(@RequestParam int badgeId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));;
        userBadgeService.setBadgeDisplayed(user.getUserID(), badgeId);
        return ResponseEntity.ok().build();
    }
}


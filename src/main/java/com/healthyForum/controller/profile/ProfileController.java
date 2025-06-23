package com.healthyForum.controller.profile;

import com.healthyForum.model.User;
import com.healthyForum.model.badge.Badge;
import com.healthyForum.model.badge.UserBadge;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.badge.BadgeService;
import com.healthyForum.service.badge.UserBadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;

    public ProfileController(UserRepository userRepository, BadgeService badgeService, UserBadgeService userBadgeService) {
        this.userRepository = userRepository;
        this.badgeService = badgeService;
        this.userBadgeService = userBadgeService;
    }

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName(); // works for both local and OAuth2 users

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found in database: " + username));

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

        model.addAttribute("user", user);
        model.addAttribute("displayedBadges", displayedBadges);
        model.addAttribute("unlockedBadges", unlockedBadges);
        model.addAttribute("lockedBadges", lockedBadges);



        return "profile/profile";
    }

//    @PostMapping("display-badge")
//    @ResponseBody
//    public ResponseEntity<?> addDisplayedBadge(@RequestParam int badgeId, Principal principal) {
//        User user = userRepository.findByUsername(principal.getName())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));;
//        userBadgeService.setBadgeDisplayed(user.getUserID(), badgeId);
//        return ResponseEntity.ok().build();
//    }
}

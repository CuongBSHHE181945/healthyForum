package com.healthyForum.controller.badge;

import com.healthyForum.model.User;
import com.healthyForum.model.badge.UserBadge;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.badge.BadgeService;
import com.healthyForum.service.badge.UserBadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/badge-display")
public class BadgeDisplayController {
    private final UserRepository userRepository;
    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;

    public BadgeDisplayController(UserRepository userRepository, BadgeService badgeService, UserBadgeService userBadgeService) {
        this.userRepository = userRepository;
        this.badgeService = badgeService;
        this.userBadgeService = userBadgeService;
    }

    @GetMapping
    public String badgeDisplaySetting(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));;
        List<UserBadge> userBadges = userBadgeService.getAllUnlockedByUser(user.getUserID());

        model.addAttribute("userBadges", userBadges);
        return "profile/badge-display"; // badge_display_setting.html
    }

    @PostMapping
    public String updateDisplayedBadges(@RequestParam(required = false) List<Integer> displayedBadgeIds,
                                        Principal principal,
                                        RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));;
        userBadgeService.updateDisplayedBadges(user.getUserID(), displayedBadgeIds);
        redirectAttributes.addFlashAttribute("success", "Badge display updated!");
        return "redirect:/profile";
    }
}

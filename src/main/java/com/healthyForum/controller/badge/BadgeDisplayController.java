package com.healthyForum.controller.badge;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.model.badge.UserBadge;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.service.badge.BadgeService;
import com.healthyForum.service.badge.UserBadgeService;
import com.healthyForum.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private final UserAccountRepository userAccountRepository;
    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;
    private final UserService userService;

    public BadgeDisplayController(UserRepository userRepository, UserAccountRepository userAccountRepository, BadgeService badgeService, UserBadgeService userBadgeService, UserService userService) {
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
        this.badgeService = badgeService;
        this.userBadgeService = userBadgeService;
        this.userService = userService;
    }

    @GetMapping
    public String badgeDisplaySetting(Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        
        List<UserBadge> userBadges = userBadgeService.getAllUnlockedByUser(user.getId());

        model.addAttribute("userBadges", userBadges);
        return "profile/badge-display"; // badge_display_setting.html
    }

    @PostMapping
    public String updateDisplayedBadges(@RequestParam(required = false) List<Integer> displayedBadgeIds,
                                        Principal principal,
                                        RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        
        userBadgeService.updateDisplayedBadges(user.getId(), displayedBadgeIds);
        redirectAttributes.addFlashAttribute("success", "Badge display updated!");
        return "redirect:/profile";
    }
}

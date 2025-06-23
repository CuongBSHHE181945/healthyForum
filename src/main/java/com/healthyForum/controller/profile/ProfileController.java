package com.healthyForum.controller.profile;

import com.healthyForum.model.User;
import com.healthyForum.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, @AuthenticationPrincipal Object principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            // For Google users, we stored their email as the username
            username = ((OAuth2User) principal).getAttribute("email");
        }

        if (username == null) {
            return "redirect:/login?error=unknown_user";
        }

        final String finalUsername = username;
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found in database: " + finalUsername));

        model.addAttribute("user", user);
        return "profile/profile";
    }
} 
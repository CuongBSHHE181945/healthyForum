package com.healthyForum.controller.profile;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Controller
@RequestMapping("/profile")
public class ChangePasswordController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserAccount account = null;
        if (principal instanceof UserDetails userDetails) {
            account = userAccountRepository.findByUsername(userDetails.getUsername())
                    .orElse(null);
        } else if (principal instanceof OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            if (email != null) {
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    account = userAccountRepository.findByUserId(user.getId()).orElse(null);
                }
            }
            if (account == null) {
                String googleId = oauth2User.getName();
                account = userAccountRepository.findByGoogleId(googleId).orElse(null);
            }
        }
        if (account == null) {
            model.addAttribute("error", "Cannot change password for this account. Please use your original login method.");
            return "profile/change-password";
        }
        model.addAttribute("hasPassword", account.hasPasswordAuthentication());
        model.addAttribute("canUsePassword", account.canUsePasswordAuthentication());
        model.addAttribute("hasGoogleId", account.hasGoogleAuthentication());
        model.addAttribute("hasMultipleAuth", account.hasMultipleAuthenticationMethods());
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    @Transactional
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            UserAccount account = null;
            if (principal instanceof UserDetails userDetails) {
                account = userAccountRepository.findByUsername(userDetails.getUsername())
                        .orElse(null);
            } else if (principal instanceof OAuth2User oauth2User) {
                String email = oauth2User.getAttribute("email");
                if (email != null) {
                    User user = userRepository.findByEmail(email).orElse(null);
                    if (user != null) {
                        account = userAccountRepository.findByUserId(user.getId()).orElse(null);
                    }
                }
                if (account == null) {
                    String googleId = oauth2User.getName();
                    account = userAccountRepository.findByGoogleId(googleId).orElse(null);
                }
            }
            if (account == null) {
                redirectAttributes.addFlashAttribute("error", "Cannot change password for this account. Please use your original login method.");
                return "redirect:/profile/change-password";
            }
            // Validate current password
            if (oldPassword == null || !passwordEncoder.matches(oldPassword, account.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
                return "redirect:/profile/change-password";
            }
            // Validate new password
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
                return "redirect:/profile/change-password";
            }
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters long.");
                return "redirect:/profile/change-password";
            }
            // Update password
            userAccountService.updatePassword(account, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred. Please try again.");
            return "redirect:/profile/change-password";
        }
    }
} 
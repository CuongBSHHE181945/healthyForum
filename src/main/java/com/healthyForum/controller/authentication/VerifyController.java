package com.healthyForum.controller.authentication;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.UserAccountService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Optional;

@Controller
public class VerifyController {

    private final UserRepository userRepository;
    private final UserAccountService userAccountService;

    public VerifyController(UserRepository userRepository, UserAccountService userAccountService) {
        this.userRepository = userRepository;
        this.userAccountService = userAccountService;
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
        Optional<UserAccount> accountOptional = userAccountService.findByVerificationCode(code);

        if (accountOptional.isPresent()) {
            UserAccount account = accountOptional.get();
            User user = account.getUser();
            
            if (account.isEnabled()) {
                redirectAttributes.addFlashAttribute("verificationError", "Account has already been verified.");
                return "redirect:/login";
            }

            userAccountService.enableAccount(account);

            // Manually authenticate user
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    account.getUsername(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName())));
            SecurityContextHolder.getContext().setAuthentication(auth);

            redirectAttributes.addFlashAttribute("verificationSuccess", "Your account has been successfully verified! You are now logged in.");
            return "redirect:/"; // Redirect to home page
        } else {
            redirectAttributes.addFlashAttribute("verificationError", "Invalid verification code.");
            return "redirect:/login";
        }
    }
} 
package com.healthyForum.controller.authentication;

import com.healthyForum.model.User;
import com.healthyForum.repository.UserRepository;
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

    public VerifyController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userRepository.findByVerificationCode(code);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.isEnabled()) {
                redirectAttributes.addFlashAttribute("verificationError", "Account has already been verified.");
                return "redirect:/login";
            }

            user.setEnabled(true);
            user.setVerificationCode(null); // Clear the code after use
            userRepository.save(user);

            // Manually authenticate user
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName())));
            SecurityContextHolder.getContext().setAuthentication(auth);

            redirectAttributes.addFlashAttribute("verificationSuccess", "Your account has been successfully verified! You are now logged in.");
            return "redirect:/home"; // Or a dedicated success page
        } else {
            redirectAttributes.addFlashAttribute("verificationError", "Invalid verification code.");
            return "redirect:/login";
        }
    }
} 
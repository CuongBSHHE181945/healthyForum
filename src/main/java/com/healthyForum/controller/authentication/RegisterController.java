package com.healthyForum.controller.authentication;

import com.healthyForum.model.*;
import com.healthyForum.repository.RoleRepository;
import com.healthyForum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.healthyForum.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    //autowired to Spring's interface to call default authentication method
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String register(Model model){
        User user = new User();

        model.addAttribute("user", user);
        return "userAuthentication/register";
    }

    @PostMapping
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("registerErrMsg", "Username already exists");
            return "redirect:/register";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("registerErrMsg", "This email is already registered. If you signed up with Google, please use 'Login with Google'.");
            return "redirect:/register";
        }

        if (!user.getFullname().matches("^[A-Za-z\\s]+$")) {
            redirectAttributes.addFlashAttribute("registerErrMsg", "Full name must contain only letters and spaces.");
            return "redirect:/register";
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Role 'USER' not found"));
        user.setRole(userRole);
        user.setSuspended(false);
        user.setProvider("local"); // For local registration

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Generate verification code
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);
        user.setEnabled(false); // Disable account until verified

        userRepository.save(user);
        logger.info("User {} saved successfully. Attempting to send verification email.", user.getUsername());

        // Send verification email
        try {
            emailService.sendVerificationEmail(user, request);
            logger.info("Verification email sent successfully to {}.", user.getEmail());
        } catch (Exception e) {
            // Log the exception and show a generic error
            logger.error("Error sending verification email for user {}", user.getUsername(), e);
            redirectAttributes.addFlashAttribute("registerErrMsg", "Error sending verification email. Please try again later.");
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute("registerSuccessMsg", "Registration successful! Please check your email to verify your account.");
        return "redirect:/login";
    }
}

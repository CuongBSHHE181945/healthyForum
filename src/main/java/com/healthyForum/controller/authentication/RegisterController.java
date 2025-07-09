package com.healthyForum.controller.authentication;

import com.healthyForum.model.*;
import com.healthyForum.repository.RoleRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.service.UserAccountService;
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
    
    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
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
    public String register(@ModelAttribute User user, 
                          @RequestParam String password,
                          @RequestParam String username,
                          RedirectAttributes redirectAttributes, 
                          HttpServletRequest request) {
        if (userAccountRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("registerErrMsg", "Username already exists");
            return "redirect:/register";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("registerErrMsg", "This email is already registered. If you signed up with Google, please use 'Login with Google'.");
            return "redirect:/register";
        }

        if (!user.getFullName().matches("^[A-Za-z\\s]+$")) {
            redirectAttributes.addFlashAttribute("registerErrMsg", "Full name must contain only letters and spaces.");
            return "redirect:/register";
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Role 'USER' not found"));
        user.setRole(userRole);

        // Save the user first
        User savedUser = userRepository.save(user);

        // Create user account with verification code
        String verificationCode = UUID.randomUUID().toString();
        UserAccount account = userAccountService.createLocalAccount(savedUser, username, password);
        userAccountService.setVerificationCode(account, verificationCode);

        logger.info("User {} saved successfully. Attempting to send verification email.", account.getUsername());

        // Send verification email
        try {
            emailService.sendVerificationEmail(savedUser, account, request);
            logger.info("Verification email sent successfully to {}.", savedUser.getEmail());
        } catch (Exception e) {
            // Log the exception and show a generic error
            logger.error("Error sending verification email for user {}", account.getUsername(), e);
            redirectAttributes.addFlashAttribute("registerErrMsg", "Error sending verification email. Please try again later.");
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute("registerSuccessMsg", "Registration successful! Please check your email to verify your account.");
        return "redirect:/login";
    }
}

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

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    //autowired to Spring's interface to call default authentication method
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping
    public String register(Model model){
        User user = new User();

        model.addAttribute("user", user);
        return "userAuthentication/register";
    }

    @PostMapping
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes){
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("registerErrMsg", "Username already exist");
            return "redirect:/register";
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Role 'USER' not found"));
        user.setRole(userRole);
        user.setSuspended(false);

        // 1. Save the raw password before encoding
        String rawPassword = user.getPassword();

        // 2. Encode and save to DB
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        // ✅ Authenticate the user manually
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), rawPassword);
        Authentication authentication = authenticationManager.authenticate(authToken);

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/home";
    }
}

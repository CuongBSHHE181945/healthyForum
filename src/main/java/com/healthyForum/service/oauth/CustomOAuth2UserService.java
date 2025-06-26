package com.healthyForum.service.oauth;

import com.healthyForum.model.Role;
import com.healthyForum.model.User;
import com.healthyForum.repository.RoleRepository;
import com.healthyForum.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getName(); // This is the unique Google ID

        // Try to find by Google ID first (for returning users)
        Optional<User> userByGoogleId = userRepository.findByGoogleId(googleId);
        if (userByGoogleId.isPresent()) {
            return oauth2User;
        }

        // Try to find by email (for users who registered locally)
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            // Link Google ID to this user (do NOT change username)
            user.setGoogleId(googleId);
            user.setProvider("google");
            user.setEnabled(true);
            userRepository.save(user);
            return oauth2User;
        }

        // No user found, create new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullname(name);
        newUser.setUsername(googleId); // Use Google ID as username for new Google users
        newUser.setGoogleId(googleId);
        newUser.setProvider("google");
        newUser.setEnabled(true); // Enable the user

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        newUser.setRole(userRole);

        userRepository.save(newUser);
        return oauth2User;
    }
} 
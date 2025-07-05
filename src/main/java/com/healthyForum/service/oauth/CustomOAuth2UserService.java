package com.healthyForum.service.oauth;

import com.healthyForum.model.Role;
import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.repository.RoleRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserAccountRepository userAccountRepository;

    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository, UserAccountRepository userAccountRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getName(); // This is the unique Google ID

        logger.info("Processing OAuth login for email: {}, Google ID: {}", email, googleId);

        // Try to find by Google ID first (for returning users)
        Optional<UserAccount> accountByGoogleId = userAccountRepository.findByGoogleId(googleId);
        if (accountByGoogleId.isPresent()) {
            UserAccount account = accountByGoogleId.get();
            logger.info("Found existing user by Google ID: {}", account.getUsername());
            return oauth2User;
        }

        // Try to find by email (for users who registered locally)
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            UserAccount account = userAccountRepository.findByUserId(user.getId()).orElse(null);
            
            logger.info("Found existing user by email: {} (provider: {}), linking Google ID", 
                       account != null ? account.getUsername() : "no account", 
                       account != null ? account.getProvider() : "none");
            
            // Link Google ID to this user's account
            if (account == null) {
                // Create account if it doesn't exist
                account = new UserAccount();
                account.setUser(user);
                account.setProvider("local");
                account.setEnabled(true);
                account.setSuspended(false);
            }
            
            account.setGoogleId(googleId);
            account.setEnabled(true);
            userAccountRepository.save(account);
            
            logger.info("Successfully linked Google ID to existing local user: {}", account.getUsername());
            return oauth2User;
        }

        // No user found, create new user
        logger.info("Creating new user for Google OAuth: {}", email);
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(name);

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        newUser.setRole(userRole);

        // Save the user first
        User savedUser = userRepository.save(newUser);
        
        // Create Google account
        UserAccount googleAccount = new UserAccount();
        googleAccount.setUsername(googleId);
        googleAccount.setGoogleId(googleId);
        googleAccount.setProvider("google");
        googleAccount.setEnabled(true);
        googleAccount.setUser(savedUser);
        userAccountRepository.save(googleAccount);
        
        logger.info("Successfully created new Google OAuth user: {}", googleAccount.getUsername());
        return oauth2User;
    }
} 
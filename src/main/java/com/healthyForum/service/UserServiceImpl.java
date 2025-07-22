package com.healthyForum.service;

import com.healthyForum.model.Role;
import com.healthyForum.model.Follow;
import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.repository.FollowRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private FollowRepository followRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void suspendUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            UserAccount account = user.getAccount();
            if (account == null) {
                // Create account if it doesn't exist
                account = new UserAccount();
                account.setUser(user);
                account.setProvider("local");
                account.setEnabled(false);
            }
            
            account.setSuspended(true);
            userAccountRepository.save(account);
        } catch (Exception e) {
            throw new RuntimeException("Error suspending user: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void unsuspendUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            UserAccount account = user.getAccount();
            if (account == null) {
                // Create account if it doesn't exist
                account = new UserAccount();
                account.setUser(user);
                account.setProvider("local");
                account.setEnabled(false);
            }
            
            account.setSuspended(false);
            userAccountRepository.save(account);
        } catch (Exception e) {
            throw new RuntimeException("Error unsuspending user: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(Principal principal) {
        if (principal == null) {
            logger.warn("getCurrentUser: principal is null");
            return null;
        }
        String name = principal.getName();
        logger.info("getCurrentUser(Principal): principal.getName() = {}", name);
        // Try by email
        User user = userRepository.findByEmail(name).orElse(null);
        if (user != null) {
            logger.info("getCurrentUser(Principal): found user by email");
            return user;
        }
        // Try by username via UserAccount
        UserAccount account = userAccountRepository.findByUsername(name).orElse(null);
        if (account != null && account.getUser() != null) {
            logger.info("getCurrentUser(Principal): found user by username via UserAccount");
            return account.getUser();
        }
        // Try by Google ID via UserAccount
        account = userAccountRepository.findByGoogleId(name).orElse(null);
        if (account != null && account.getUser() != null) {
            logger.info("getCurrentUser(Principal): found user by GoogleId via UserAccount");
            return account.getUser();
        }
        // Try by Google ID via UserRepository
        user = userRepository.findByGoogleId(name).orElse(null);
        if (user != null) {
            logger.info("getCurrentUser(Principal): found user by GoogleId via UserRepository");
            return user;
        }
        logger.warn("getCurrentUser(Principal): user not found");
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(Object principal) {
        if (principal == null) {
            logger.warn("getCurrentUser: principal is null");
            return null;
        }
        logger.info("getCurrentUser(Object): principal class = {}", principal.getClass().getName());
        if (principal instanceof Principal p) {
            return getCurrentUser(p);
        } else if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            logger.info("getCurrentUser(UserDetails): username = {}", username);
            // Try by username via UserAccount
            UserAccount account = userAccountRepository.findByUsername(username).orElse(null);
            if (account != null && account.getUser() != null) {
                logger.info("getCurrentUser(UserDetails): found user by username via UserAccount");
                return account.getUser();
            }
            // Try by email
            User user = userRepository.findByEmail(username).orElse(null);
            if (user != null) {
                logger.info("getCurrentUser(UserDetails): found user by email");
                return user;
            }
            // Try by Google ID via UserAccount
            account = userAccountRepository.findByGoogleId(username).orElse(null);
            if (account != null && account.getUser() != null) {
                logger.info("getCurrentUser(UserDetails): found user by GoogleId via UserAccount");
                return account.getUser();
            }
            // Try by Google ID via UserRepository
            user = userRepository.findByGoogleId(username).orElse(null);
            if (user != null) {
                logger.info("getCurrentUser(UserDetails): found user by GoogleId via UserRepository");
                return user;
            }
        } else if (principal instanceof OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            logger.info("getCurrentUser(OAuth2User): email = {}", email);
            if (email != null) {
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    logger.info("getCurrentUser(OAuth2User): found user by email");
                    return user;
                }
            }
            String googleId = oauth2User.getName();
            logger.info("getCurrentUser(OAuth2User): googleId = {}", googleId);
            if (googleId != null) {
                UserAccount account = userAccountRepository.findByGoogleId(googleId).orElse(null);
                if (account != null && account.getUser() != null) {
                    logger.info("getCurrentUser(OAuth2User): found user by GoogleId via UserAccount");
                    return account.getUser();
                }
                User user = userRepository.findByGoogleId(googleId).orElse(null);
                if (user != null) {
                    logger.info("getCurrentUser(OAuth2User): found user by GoogleId via UserRepository");
                    return user;
                }
            }
        }
        logger.warn("getCurrentUser(Object): user not found");
        return null;
    }

    @Override
    public boolean isAdminOrModerator(Role role){
        String roleName = role.getRoleName();
        if ("ADMIN".equals(roleName)||"MODERATOR".equals(roleName))
            return true;
        return false;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public boolean isFollowing(User follower, User followed) {
        return followRepository.existsByFollowerAndFollowed(follower, followed);
    }

    @Transactional
    @Override
    public void follow(User follower, User followed) {
        if (!isFollowing(follower, followed)) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowed(followed);
            followRepository.save(follow);
        }
    }

    @Transactional
    @Override
    public void unfollow(User follower, User followed) {
        followRepository.deleteByFollowerAndFollowed(follower, followed);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        // First, try to find by username in the UserAccount table, which is the most direct way.
        UserAccount account = userAccountRepository.findByUsername(username).orElse(null);
        if (account != null && account.getUser() != null) {
            return account.getUser();
        }

        // As a fallback, try to find by email, as username might be an email for some users.
        return userRepository.findByEmail(username).orElse(null);
    }
}




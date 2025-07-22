package com.healthyForum.service;

import com.healthyForum.model.Role;
import com.healthyForum.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.security.Principal;

@Service
public interface UserService {
       public List<User> getAllUsers();
       void suspendUser(Long userId);
       void unsuspendUser(Long userId);
       User getCurrentUser(Principal principal);
       User getCurrentUser(Object principal);
       boolean isAdminOrModerator(Role role);
       User getUserById(Long id);
       boolean isFollowing(User follower, User followed);
       void follow(User follower, User followed);
       void unfollow(User follower, User followed);
       User findByUsername(String username);
}

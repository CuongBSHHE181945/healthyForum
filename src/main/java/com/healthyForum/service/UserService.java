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
}

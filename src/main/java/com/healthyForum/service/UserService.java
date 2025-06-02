package com.healthyForum.service;

import com.healthyForum.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
       public List<User> getAllUsers();
       void suspendUser(Long userId);
       void unsuspendUser(Long userId);


}

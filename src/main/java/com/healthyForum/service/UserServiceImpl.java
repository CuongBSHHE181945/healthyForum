package com.healthyForum.service;

import com.healthyForum.model.User;
import com.healthyForum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

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
            user.setSuspended(true);
            userRepository.save(user);
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
            user.setSuspended(false);
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error unsuspending user: " + e.getMessage());
        }
    }
}




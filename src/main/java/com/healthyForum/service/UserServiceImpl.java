package com.healthyForum.service;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;

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
}




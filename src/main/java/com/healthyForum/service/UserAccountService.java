package com.healthyForum.service;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserAccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserAccount createLocalAccount(User user, String username, String password) {
        UserAccount account = new UserAccount();
        account.setUser(user);
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setProvider("local");
        account.setEnabled(false);
        account.setSuspended(false);
        
        return userAccountRepository.save(account);
    }

    public UserAccount createGoogleAccount(User user, String googleId) {
        UserAccount account = new UserAccount();
        account.setUser(user);
        account.setUsername(googleId);
        account.setGoogleId(googleId);
        account.setProvider("google");
        account.setEnabled(true);
        account.setSuspended(false);
        
        return userAccountRepository.save(account);
    }

    public void enableAccount(UserAccount account) {
        account.setEnabled(true);
        userAccountRepository.save(account);
    }

    public void suspendAccount(UserAccount account) {
        account.setSuspended(true);
        userAccountRepository.save(account);
    }

    public void unsuspendAccount(UserAccount account) {
        account.setSuspended(false);
        userAccountRepository.save(account);
    }

    public void updatePassword(UserAccount account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(account);
    }

    public void updateLastLogin(UserAccount account) {
        account.updateLastLogin();
        userAccountRepository.save(account);
    }

    public void setVerificationCode(UserAccount account, String verificationCode) {
        account.setVerificationCode(verificationCode);
        userAccountRepository.save(account);
    }

    public void setResetPasswordToken(UserAccount account, String resetToken, LocalDateTime expiry) {
        account.setResetPasswordToken(resetToken);
        account.setResetTokenExpiry(expiry);
        userAccountRepository.save(account);
    }

    public void clearResetPasswordToken(UserAccount account) {
        account.setResetPasswordToken(null);
        account.setResetTokenExpiry(null);
        userAccountRepository.save(account);
    }

    public UserAccount createAccountForUser(User user, String username, String password, String provider) {
        UserAccount account = new UserAccount();
        account.setUser(user);
        account.setUsername(username);
        
        if ("local".equals(provider)) {
            account.setPassword(passwordEncoder.encode(password));
            account.setEnabled(false);
        } else if ("google".equals(provider)) {
            account.setGoogleId(username); // For Google, username is the Google ID
            account.setEnabled(true);
        }
        
        account.setProvider(provider);
        account.setSuspended(false);
        
        return userAccountRepository.save(account);
    }

    public Optional<UserAccount> findByVerificationCode(String verificationCode) {
        return userAccountRepository.findByVerificationCode(verificationCode);
    }

    public Optional<UserAccount> findByResetPasswordToken(String resetPasswordToken) {
        return userAccountRepository.findByResetPasswordToken(resetPasswordToken);
    }

    public Optional<UserAccount> findByGoogleId(String googleId) {
        return userAccountRepository.findByGoogleId(googleId);
    }

    private UserAccount getOrCreateAccount(User user) {
        UserAccount account = user.getAccount();
        if (account == null) {
            account = new UserAccount();
            account.setUser(user);
            account.setProvider("local");
            account.setEnabled(false);
            account.setSuspended(false);
        }
        return account;
    }
} 
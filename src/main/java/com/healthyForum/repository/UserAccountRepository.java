package com.healthyForum.repository;

import com.healthyForum.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUserId(Long userId);
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByGoogleId(String googleId);
    Optional<UserAccount> findByVerificationCode(String verificationCode);
    Optional<UserAccount> findByResetPasswordToken(String resetPasswordToken);
} 
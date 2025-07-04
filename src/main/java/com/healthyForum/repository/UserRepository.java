package com.healthyForum.repository;

import com.healthyForum.model.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationCode(String code);

    Optional<User> findByResetPasswordToken(String token);

    Optional<User> findByGoogleId(String googleId);
}

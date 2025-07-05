package com.healthyForum.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true)
    private String password;

    @Column(name = "provider", nullable = false)
    private String provider = "local";

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;

    @Column(name = "suspended", nullable = false)
    private boolean suspended = false;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(name = "reset_password_token", length = 64)
    private String resetPasswordToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Authentication helper methods
    public boolean hasPasswordAuthentication() {
        return password != null && !password.isEmpty();
    }

    public boolean hasGoogleAuthentication() {
        return googleId != null && !googleId.isEmpty();
    }

    public boolean canUsePasswordAuthentication() {
        return hasPasswordAuthentication() && "local".equals(provider);
    }

    public boolean canUseGoogleAuthentication() {
        return hasGoogleAuthentication();
    }

    public String getPrimaryAuthenticationMethod() {
        if (hasPasswordAuthentication() && "local".equals(provider)) {
            return "local";
        } else if (hasGoogleAuthentication()) {
            return "google";
        }
        return provider;
    }

    public boolean hasMultipleAuthenticationMethods() {
        return hasPasswordAuthentication() && hasGoogleAuthentication();
    }

    public boolean isActive() {
        return enabled && !suspended;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
} 
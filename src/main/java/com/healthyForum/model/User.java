package com.healthyForum.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID")
    private Long userID;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String fullname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String gender;

    @Column(nullable = true)
    private LocalDate dob;

    @Column(nullable = true)
    private String address;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "suspended", nullable = false)
    private boolean suspended = false; // Set default value

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(name = "enabled")
    private boolean enabled = false;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "reset_password_token", length = 64)
    private String resetPasswordToken;

    @Column(name = "reset_token_expiry")
    private java.time.LocalDateTime resetTokenExpiry;

    public User() {
    }

    public User(Long userID, Role role, boolean suspended, String address, LocalDate dob, String gender, String email, String fullname, String password, String username, String provider) {
        this.userID = userID;
        this.role = role;
        this.suspended = suspended;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.fullname = fullname;
        this.password = password;
        this.username = username;
        this.provider = provider;
        this.enabled = false; // Default for new users
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public java.time.LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(java.time.LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }
}
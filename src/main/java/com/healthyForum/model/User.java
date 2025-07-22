package com.healthyForum.model;

import com.healthyForum.model.Post.Post;
import com.healthyForum.model.Enum.Visibility;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String gender;

    @Column(nullable = true)
    private LocalDate dob;

    @Column(nullable = true)
    private String address;

    // Health-related fields from HealthAssessment
    @Column(nullable = true)
    private Integer age;

    @Column(nullable = true)
    private Double height;

    @Column(nullable = true)
    private Double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserAccount account;

    @Column(name = "profile_visibility")
    @Enumerated(EnumType.STRING)
    private Visibility profileVisibility = Visibility.PUBLIC;

    // Helper methods
    public int calculateAge() {
        if (dob != null) {
            LocalDate today = LocalDate.now();
            int calculatedAge = today.getYear() - dob.getYear();
            
            // Adjust if birthday hasn't occurred this year
            if (today.getMonthValue() < dob.getMonthValue() || 
                (today.getMonthValue() == dob.getMonthValue() && today.getDayOfMonth() < dob.getDayOfMonth())) {
                calculatedAge--;
            }
            
            return calculatedAge;
        }
        return age != null ? age : 0;
    }

    public boolean isAdult() {
        return calculateAge() >= 18;
    }

    public String getDisplayName() {
        return fullName != null ? fullName : (account != null ? account.getUsername() : "Unknown");
    }

    public boolean hasAccount() {
        return account != null;
    }

    public String getUsername() {
        return account != null ? account.getUsername() : null;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public UserAccount getUserAccount() {
        return account;
    }
}
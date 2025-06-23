package com.healthyForum.model.badge;

import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="user_badge")
public class UserBadge {
    // 🔗 Link to User
    @EmbeddedId
    private UserBadgeId id;

    @ManyToOne
    @MapsId("userId") // 👈 maps to id.userId
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("badgeId") // 👈 maps to id.badgeId
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @Column(name = "earned_at")
    private LocalDateTime earnedAt;

    @Column(name = "displayed" , nullable = false)
    private boolean displayed = false;
}

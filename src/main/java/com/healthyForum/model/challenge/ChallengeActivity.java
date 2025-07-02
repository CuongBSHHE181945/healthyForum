package com.healthyForum.model.challenge;

import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "challenge_activity")
public class ChallengeActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_challenge_id", nullable = false)
    private CommunityChallenge communityChallenge;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_type", nullable = false)
    private String activityType; // PROGRESS_UPDATE, PHOTO_UPLOAD, COMMENT, CHEER

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "points_earned")
    private Integer pointsEarned = 0;

    @Column(name = "activity_date", nullable = false)
    private LocalDateTime activityDate = LocalDateTime.now();

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "comments_count")
    private Integer commentsCount = 0;
}
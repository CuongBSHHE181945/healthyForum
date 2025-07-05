package com.healthyForum.model.badge;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
//Serializable 	A marker interface to allow object → byte stream
public class UserBadgeId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "badge_id")
    private int badgeId;

    public UserBadgeId() {
    }

    public UserBadgeId(Long userId, int badgeId) {
        this.userId = userId;
        this.badgeId = badgeId;
    }
}

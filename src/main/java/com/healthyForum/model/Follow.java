package com.healthyForum.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "follow")
@Data

    public class Follow {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "follower_id", nullable = false)
        private User follower;

        @ManyToOne
        @JoinColumn(name = "followed_id", nullable = false)
        private User followed;
}

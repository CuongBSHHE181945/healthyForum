package com.healthyForum.model.challenge;

import com.healthyForum.model.Enum.ReactionType;
import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "evidence_react", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "evidence_post_id"})
})
public class EvidenceReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "evidence_post_id", nullable = false)
    private EvidencePost evidencePost;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;
}


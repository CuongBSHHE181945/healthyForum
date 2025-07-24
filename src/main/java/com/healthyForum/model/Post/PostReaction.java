package com.healthyForum.model.Post;

import com.healthyForum.model.Enum.ReactionType;
import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="post_reaction")
public class PostReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne

    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}

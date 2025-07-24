package com.healthyForum.model.Post;

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

    private boolean liked; // true = like, false = dislike

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

}

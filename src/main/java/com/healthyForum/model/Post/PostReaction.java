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
    private Long id;

    private boolean liked; // true = like, false = dislike

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

}

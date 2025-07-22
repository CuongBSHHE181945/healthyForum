package com.healthyForum.model.Post;

import com.healthyForum.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_post_id")
    private Post post;

}

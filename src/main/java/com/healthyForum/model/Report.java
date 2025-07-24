package com.healthyForum.model;

import com.healthyForum.model.Post.Post;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    // The reported content could be a post, comment, or user
    @ManyToOne
    @JoinColumn(name = "reported_post_id")
    private Post reportedPost;

    @ManyToOne
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt = new Date();

    @Column(columnDefinition = "TEXT")
    private String resolution;

    // Enum for report status
    public enum ReportStatus {
        PENDING, RESOLVED, REJECTED
    }
}
package com.healthyForum.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@jakarta.persistence.Entity
@Data
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long Id;

    @ManyToOne
    @JoinColumn(name = "sender_Id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "sleepEntries"})
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_Id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "sleepEntries"})
    private User receiver;

    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Boolean isRead = false;

    @Transient
    private boolean isSender;

    public Message(){
    }

    public Message(User sender, User receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public void setIsSender(boolean isSender) { this.isSender = isSender; }
}

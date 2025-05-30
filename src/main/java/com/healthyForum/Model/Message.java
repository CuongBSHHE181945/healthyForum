package com.healthyForum.Model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long Id;

    @ManyToOne
    @JoinColumn(name = "sender_Id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_Id")
    private User receiver;

    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Boolean isRead = false;

    public Message(){
    }

    public Message(User sender, User receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSenderId(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}

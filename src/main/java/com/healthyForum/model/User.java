package com.healthyForum.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false)
    private String address;

    @Column(name = "suspended", nullable = false)
    private boolean suspended = false; // Set default value

    public User() {
    }

    // Getters and setters...
    public User(Long userID, String username, String password, String fullname, String gender, String email, LocalDate dob, String address) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
    }
        this.email = email;
        this.dob = dob;
        this.address = address;
    }
}

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }
}
    package com.healthyForum.model;

    import jakarta.persistence.*;
    import lombok.Data;

    import java.time.LocalDate;

    @Entity
    @Data
    @Table(name = "USER")
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

        // Getters and setters...
    }
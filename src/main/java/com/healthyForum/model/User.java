package com.healthyForum.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "user")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userID;

    @Column(name = "username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="fullname")
    private String fullname;

    @Column(name="email")
    private String email;

    @Column(name="gender")
    private boolean gender;

    @Column(name="dob")
    private Date dob;

    @Column(name="address")
    private String address;

    public User() {
    }

    public User(String username, String password, String fullname, String email, boolean gender, Date dob, String address) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
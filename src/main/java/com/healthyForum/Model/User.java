package com.healthyForum.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "USER")
public class User {

    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name="password")
    private String password;
}

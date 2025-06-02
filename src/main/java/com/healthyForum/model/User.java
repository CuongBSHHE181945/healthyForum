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
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name="password")
    private String password;

    public User() {
    }

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
<<<<<<< HEAD
=======
        this.fullname = fullname;
        this.gender = gender;
        this.email = email;
        this.dob = dob;
        this.address = address;
>>>>>>> 4a45bbaf385129bb891531ff49c0b6b9e85ad48c
    }
}

package com.healthyForum.repository;

import com.healthyForum.model.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u JOIN u.account a WHERE a.googleId = :googleId")
    Optional<User> findByGoogleId(@Param("googleId") String googleId);
}

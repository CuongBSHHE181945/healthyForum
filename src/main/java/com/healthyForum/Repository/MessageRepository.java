package com.healthyForum.repository;
import com.healthyForum.model.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.userID = :user1 AND m.receiver.userID = :user2) OR " +
            "(m.sender.userID = :user2 AND m.receiver.userID = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<Message> getConversation(@Param("user1") Long user1Id,
                                  @Param("user2") Long user2Id);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.userID = :receiverId AND m.isRead = false")
    long countUnreadMessages(@Param("receiverId") Long receiverId);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.id = :messageId")
    void markMessageAsRead(@Param("messageId") Long messageId);
}


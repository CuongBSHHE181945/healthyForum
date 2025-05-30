package com.healthyForum.Repository;
import com.healthyForum.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface MessageRepository extends JpaRepository<Message, Long>{
    List<Message> findBySender_IdAndReceiver_IdOrReceiver_IdAndSender_IdOrderByTimestampAsc(Long senderId, Long receiverId, Long receiverId2, Long senderId2);

    long countByReceiver_IdAndIsReadFalse(Long receiverId);
}

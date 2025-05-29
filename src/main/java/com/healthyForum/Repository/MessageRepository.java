package com.healthyForum.Repository;
import com.healthyForum.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface MessageRepository extends JpaRepository<Message, Long>{
    List<Message> findByEntity(Long senderId, Long receiverId, Long receiverId2, Long senderId2);

    long countByReceiverIdAndIsReadFalse(Long receiverId);
}

package com.healthyForum.Service;

import com.healthyForum.Model.Message;
import java.util.List;

public interface MessageService {
    Message sendMessage(Message message);
    List<Message> getConversation(Long userId1, Long userId2);
    Message markMessageAsRead(Long messageId);
    void deleteMessage(Long messageId);
    long countUnreadMessages(Long userId);
}

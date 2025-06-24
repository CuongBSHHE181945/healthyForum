package com.healthyForum.service;

import com.healthyForum.model.Message;

import java.security.Principal;
import java.util.List;

public interface MessageService {
    Message sendMessage(Message message);
    List<Message> getConversation(Long receiverId, Principal principal);
    long countUnreadMessages(Long receiverId);
    Message markMessageAsRead(Long messageId);
}

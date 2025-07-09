package com.healthyForum.service;

import com.healthyForum.model.Message;
import com.healthyForum.model.User;
import com.healthyForum.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.healthyForum.repository.UserRepository;


import java.security.Principal;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getConversation(Long receiverId, Principal principal) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail("alice@example.com")
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> messages = messageRepository.getConversation(currentUser.getUserID(), receiverId);

        // Gắn cờ isSender
        for (Message message : messages) {
            boolean isSender = message.getSender().getUserID().equals(currentUser.getUserID());
            message.setIsSender(isSender);
        }

        return messages;
    }


    @Override
    public long countUnreadMessages(Long receiverId) {
        return messageRepository.countUnreadMessages(receiverId);
    }

    @Override
    @Transactional
    public Message markMessageAsRead(Long messageId) {
        messageRepository.markMessageAsRead(messageId);
        return messageRepository.findById(messageId).orElse(null);
    }
}

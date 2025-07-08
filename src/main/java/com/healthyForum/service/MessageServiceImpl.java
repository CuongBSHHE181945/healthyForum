package com.healthyForum.service;

import com.healthyForum.model.Message;
import com.healthyForum.model.User;
import com.healthyForum.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.model.UserAccount;
import com.healthyForum.service.UserService;

import java.security.Principal;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserService userService;

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, UserAccountRepository userAccountRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
        this.userService = userService;
    }

    @Override
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getConversation(Long receiverId, Principal principal) {
        User currentUser = userService.getCurrentUser(principal);
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }

        List<Message> messages = messageRepository.getConversation(currentUser.getId(), receiverId);

        // Gắn cờ isSender
        for (Message message : messages) {
            boolean isSender = message.getSender().getId().equals(currentUser.getId());
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

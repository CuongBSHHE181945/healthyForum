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

import java.security.Principal;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, UserAccountRepository userAccountRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getConversation(Long receiverId, Principal principal) {
        User currentUser = getCurrentUser(principal);
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

    /**
     * Helper method to get current user from Principal (handles both local and OAuth authentication)
     */
    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        String principalName = principal.getName();
        UserAccount account = userAccountRepository.findByUsername(principalName)
                .orElseGet(() -> userAccountRepository.findByGoogleId(principalName).orElse(null));
        if (account != null) {
            return account.getUser();
        }
        User user = userRepository.findByEmail(principalName).orElse(null);
        if (user != null) {
            return user;
        }
        // If principal is OAuth2User, try to get email and find by email
        if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            if (email != null) {
                return userRepository.findByEmail(email).orElse(null);
            }
            String googleId = oauth2User.getName();
            if (googleId != null) {
                UserAccount acc = userAccountRepository.findByGoogleId(googleId).orElse(null);
                if (acc != null) return acc.getUser();
            }
        }
        return null;
    }
}

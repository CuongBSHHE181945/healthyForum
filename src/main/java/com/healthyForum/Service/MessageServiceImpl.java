package com.healthyForum.Service;

import com.healthyForum.Model.Message;
import com.healthyForum.Repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findByEntity(userId1, userId2, userId1, userId2);
    }

    @Override
    public Message markMessageAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with id: " + messageId));
        message.setRead(true);
        return messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Long messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new IllegalArgumentException("Message not found with id: " + messageId);
        }
        messageRepository.deleteById(messageId);
    }

    @Override
    public long countUnreadMessages(Long userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }
}

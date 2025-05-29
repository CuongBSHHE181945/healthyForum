package com.healthyForum.Controller;

import com.healthyForum.Model.Message;
import com.healthyForum.Service.MessageServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageServiceImpl messageServiceImpl;

    public MessageController(MessageServiceImpl messageServiceImpl) {
        this.messageServiceImpl = messageServiceImpl;
    }

    // ✅ Send a message
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        Message saved = messageServiceImpl.sendMessage(message);
        return ResponseEntity.ok(saved);
    }

    // ✅ Get all messages between two users
    @GetMapping("/{senderId}/{receiverId}")
    public ResponseEntity<List<Message>> getConversation(@PathVariable Long senderId,
                                                         @PathVariable Long receiverId) {
        List<Message> messages = messageServiceImpl.getConversation(senderId, receiverId);
        return ResponseEntity.ok(messages);
    }

    // ✅ Mark a message as read
    @PutMapping("/{messageId}/read")
    public ResponseEntity<Message> markAsRead(@PathVariable Long messageId) {
        Message updated = messageServiceImpl.markMessageAsRead(messageId);
        return ResponseEntity.ok(updated);
    }

    // ✅ Delete a message by ID
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageServiceImpl.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    // ✅ Get count of unread messages for a user
    @GetMapping("/unread/{userId}")
    public ResponseEntity<Long> countUnreadMessages(@PathVariable Long userId) {
        long count = messageServiceImpl.countUnreadMessages(userId);
        return ResponseEntity.ok(count);
    }
}

package com.healthyForum.controller;

import com.healthyForum.model.Message;
import com.healthyForum.model.User;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.MessageServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageServiceImpl messageServiceImpl;
    private final UserRepository userRepository;

    public MessageController(MessageServiceImpl messageServiceImpl, UserRepository userRepository) {
        this.messageServiceImpl = messageServiceImpl;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showMessagePage() {
        return "Messaging";
    }

//    @PostMapping
//    public ResponseEntity<Message> sendMessage(@RequestBody Message message, Principal principal) {
//        String email = principal.getName();
//        User sender = userRepository.findByEmail("alice@example.com")
//                .orElseThrow(() -> new RuntimeException("User not found: " + email));
//        message.setSender(sender);
//
//        Message saved = messageServiceImpl.sendMessage(message);
//        return ResponseEntity.ok(saved);
//    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        // Fake "logged-in" user
        User sender = userRepository.findByEmail("alice@example.com")
                .orElseThrow(() -> new RuntimeException("Fake user not found"));

        User receiver = userRepository.findById(message.getReceiver().getUserID())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        message.setSender(sender);
        message.setReceiver(receiver);

        Message saved = messageServiceImpl.sendMessage(message);
        return ResponseEntity.ok(saved);
    }

//    @GetMapping("/{receiverId}")
//    public ResponseEntity<List<Message>> getConversation(@PathVariable Long receiverId, Principal principal) {
//        return ResponseEntity.ok(messageServiceImpl.getConversation(receiverId, principal));
//    }

    @GetMapping("/{receiverId}")
    public ResponseEntity<List<Message>> getConversation(@PathVariable Long receiverId) {
        // Fake logged-in user
        Principal fakePrincipal = () -> "alice@example.com";

        List<Message> messages = messageServiceImpl.getConversation(receiverId, fakePrincipal);
        return ResponseEntity.ok(messages);
    }
    @GetMapping("/unread/count/{receiverId}")
    public ResponseEntity<Long> countUnreadMessages(@PathVariable Long receiverId) {
        return ResponseEntity.ok(messageServiceImpl.countUnreadMessages(receiverId));
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<Message> markAsRead(@PathVariable Long messageId) {
        return ResponseEntity.ok(messageServiceImpl.markMessageAsRead(messageId));
    }
}

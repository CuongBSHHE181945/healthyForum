package com.healthyForum.service;

import com.healthyForum.model.Feedback;
import com.healthyForum.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    // In src/main/java/com/healthyForum/service/FeedbackService.java

    public void respondToFeedback(Long id, String response) {
        Feedback feedback = feedbackRepository.findById(id).orElse(null);
        if (feedback != null) {
            feedback.setResponse(response);
            feedbackRepository.save(feedback);
        }
    }
}

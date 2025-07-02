package com.healthyForum.controller.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.ChallengeActivity;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.challenge.ChallengeProgressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/progress")
public class ProgressTrackingController {

    private final ChallengeProgressService progressService;
    private final UserRepository userRepository;

    public ProgressTrackingController(ChallengeProgressService progressService, UserRepository userRepository) {
        this.progressService = progressService;
        this.userRepository = userRepository;
    }

    @GetMapping("/challenge/{challengeId}")
    public String showProgressDashboard(@PathVariable Long challengeId, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        
        List<ChallengeActivity> userActivities = progressService.getUserActivities(user, challengeId);
        List<ChallengeActivity> recentActivities = progressService.getRecentActivities(challengeId, 7);
        Integer totalPoints = progressService.getUserTotalPoints(user, challengeId);
        Double progressPercentage = progressService.calculateProgressPercentage(user, challengeId);
        
        model.addAttribute("challengeId", challengeId);
        model.addAttribute("userActivities", userActivities);
        model.addAttribute("recentActivities", recentActivities);
        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("progressPercentage", progressPercentage);
        
        return "progress/dashboard";
    }

    @GetMapping("/challenge/{challengeId}/activities")
    public String showAllActivities(@PathVariable Long challengeId, Model model, Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        List<ChallengeActivity> activities = progressService.getChallengeActivities(challengeId);
        
        model.addAttribute("challengeId", challengeId);
        model.addAttribute("activities", activities);
        
        return "progress/activities";
    }

    @GetMapping("/challenge/{challengeId}/log")
    public String showLogForm(@PathVariable Long challengeId, Model model, Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        model.addAttribute("challengeId", challengeId);
        model.addAttribute("activity", new ChallengeActivity());
        
        return "progress/log-activity";
    }

    @PostMapping("/challenge/{challengeId}/log")
    public String logActivity(@PathVariable Long challengeId,
                             @RequestParam String activityType,
                             @RequestParam String description,
                             @RequestParam(required = false) String photoUrl,
                             @RequestParam(required = false) Integer pointsEarned,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        try {
            progressService.logActivity(user, challengeId, activityType, description, photoUrl, pointsEarned);
            redirectAttributes.addFlashAttribute("success", "Activity logged successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to log activity: " + e.getMessage());
        }
        
        return "redirect:/progress/challenge/" + challengeId;
    }

    @PostMapping("/challenge/{challengeId}/photo")
    public String uploadPhoto(@PathVariable Long challengeId,
                             @RequestParam String photoUrl,
                             @RequestParam(required = false) String description,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        try {
            progressService.uploadProgressPhoto(user, challengeId, photoUrl, description);
            redirectAttributes.addFlashAttribute("success", "Progress photo uploaded successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload photo: " + e.getMessage());
        }
        
        return "redirect:/progress/challenge/" + challengeId;
    }

    @PostMapping("/challenge/{challengeId}/update")
    public String addProgressUpdate(@PathVariable Long challengeId,
                                   @RequestParam String description,
                                   @RequestParam(required = false) Integer pointsEarned,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        try {
            progressService.addProgressUpdate(user, challengeId, description, pointsEarned);
            redirectAttributes.addFlashAttribute("success", "Progress update added successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add progress update: " + e.getMessage());
        }
        
        return "redirect:/progress/challenge/" + challengeId;
    }

    @PostMapping("/challenge/{challengeId}/comment")
    public String addComment(@PathVariable Long challengeId,
                            @RequestParam String comment,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        try {
            progressService.addComment(user, challengeId, comment);
            redirectAttributes.addFlashAttribute("success", "Comment added successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add comment: " + e.getMessage());
        }
        
        return "redirect:/progress/challenge/" + challengeId + "/activities";
    }

    @PostMapping("/challenge/{challengeId}/cheer")
    public String addCheer(@PathVariable Long challengeId,
                          @RequestParam String cheerMessage,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        try {
            progressService.addCheer(user, challengeId, cheerMessage);
            redirectAttributes.addFlashAttribute("success", "Cheer sent successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send cheer: " + e.getMessage());
        }
        
        return "redirect:/progress/challenge/" + challengeId + "/activities";
    }

    // REST API endpoints for AJAX functionality
    @PostMapping("/api/activity/{activityId}/like")
    @ResponseBody
    public ResponseEntity<String> likeActivity(@PathVariable Long activityId, Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        try {
            progressService.likeActivity(activityId);
            return ResponseEntity.ok("Activity liked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to like activity: " + e.getMessage());
        }
    }

    @GetMapping("/api/challenge/{challengeId}/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChallengeStats(@PathVariable Long challengeId, Principal principal) {
        User user = getCurrentUser(principal);
        
        try {
            Integer totalPoints = progressService.getUserTotalPoints(user, challengeId);
            Double progressPercentage = progressService.calculateProgressPercentage(user, challengeId);
            
            Map<String, Object> stats = Map.of(
                "totalPoints", totalPoints,
                "progressPercentage", progressPercentage
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/challenge/{challengeId}/activities/recent")
    @ResponseBody
    public ResponseEntity<List<ChallengeActivity>> getRecentActivities(@PathVariable Long challengeId, 
                                                                       @RequestParam(defaultValue = "7") int days,
                                                                       Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        try {
            List<ChallengeActivity> activities = progressService.getRecentActivities(challengeId, days);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
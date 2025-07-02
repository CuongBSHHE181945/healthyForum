package com.healthyForum.controller.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.LeaderboardEntry;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.challenge.LeaderboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final UserRepository userRepository;

    public LeaderboardController(LeaderboardService leaderboardService, UserRepository userRepository) {
        this.leaderboardService = leaderboardService;
        this.userRepository = userRepository;
    }

    @GetMapping("/challenge/{challengeId}")
    public String showLeaderboard(@PathVariable Long challengeId, Model model, Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        List<LeaderboardEntry> individualLeaderboard = leaderboardService.getIndividualLeaderboard(challengeId);
        List<LeaderboardEntry> teamLeaderboard = leaderboardService.getTeamLeaderboard(challengeId);
        
        model.addAttribute("challengeId", challengeId);
        model.addAttribute("individualLeaderboard", individualLeaderboard);
        model.addAttribute("teamLeaderboard", teamLeaderboard);
        
        return "leaderboard/challenge-leaderboard";
    }

    @GetMapping("/api/challenge/{challengeId}/individual")
    @ResponseBody
    public ResponseEntity<List<LeaderboardEntry>> getIndividualLeaderboard(@PathVariable Long challengeId, Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        List<LeaderboardEntry> leaderboard = leaderboardService.getIndividualLeaderboard(challengeId);
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/api/challenge/{challengeId}/team")
    @ResponseBody
    public ResponseEntity<List<LeaderboardEntry>> getTeamLeaderboard(@PathVariable Long challengeId, Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        List<LeaderboardEntry> leaderboard = leaderboardService.getTeamLeaderboard(challengeId);
        return ResponseEntity.ok(leaderboard);
    }

    @PostMapping("/api/challenge/{challengeId}/refresh")
    @ResponseBody
    public ResponseEntity<String> refreshLeaderboard(@PathVariable Long challengeId, Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        try {
            leaderboardService.updateAllLeaderboards(challengeId);
            return ResponseEntity.ok("Leaderboard refreshed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to refresh leaderboard: " + e.getMessage());
        }
    }

    @GetMapping("/api/challenge/{challengeId}/user-rank")
    @ResponseBody
    public ResponseEntity<Integer> getUserRank(@PathVariable Long challengeId, Principal principal) {
        User user = getCurrentUser(principal);
        
        Integer rank = leaderboardService.getUserRank(challengeId, user.getUserID());
        return ResponseEntity.ok(rank);
    }

    @GetMapping("/api/challenge/{challengeId}/team/{teamId}/rank")
    @ResponseBody
    public ResponseEntity<Integer> getTeamRank(@PathVariable Long challengeId, @PathVariable Long teamId, Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        Integer rank = leaderboardService.getTeamRank(challengeId, teamId);
        return ResponseEntity.ok(rank);
    }

    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
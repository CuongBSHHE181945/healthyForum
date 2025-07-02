package com.healthyForum.controller.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.CommunityChallenge;
import com.healthyForum.model.challenge.ChallengeParticipation;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.challenge.ChallengeCategoryRepository;
import com.healthyForum.service.challenge.CommunityChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/community-challenges")
public class CommunityChallengeController {

    private final CommunityChallengeService communityChallengeService;
    private final UserRepository userRepository;
    private final ChallengeCategoryRepository categoryRepository;

    public CommunityChallengeController(CommunityChallengeService communityChallengeService,
                                      UserRepository userRepository,
                                      ChallengeCategoryRepository categoryRepository) {
        this.communityChallengeService = communityChallengeService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String listChallenges(Model model, Principal principal, 
                                @RequestParam(value = "category", required = false) Integer categoryId) {
        User user = getCurrentUser(principal);
        
        List<CommunityChallenge> activeChallenges;
        if (categoryId != null) {
            activeChallenges = communityChallengeService.getChallengesByCategory(categoryId);
        } else {
            activeChallenges = communityChallengeService.getAllActiveChallenges();
        }
        
        List<CommunityChallenge> upcomingChallenges = communityChallengeService.getAllUpcomingChallenges();
        List<ChallengeParticipation> userParticipations = communityChallengeService.getUserParticipations(user);
        
        model.addAttribute("activeChallenges", activeChallenges);
        model.addAttribute("upcomingChallenges", upcomingChallenges);
        model.addAttribute("userParticipations", userParticipations);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("selectedCategory", categoryId);
        
        return "community-challenge/list";
    }

    @GetMapping("/{id}")
    public String viewChallenge(@PathVariable Long id, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        
        Optional<CommunityChallenge> challengeOpt = communityChallengeService.getChallengeById(id);
        if (challengeOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found");
        }
        
        CommunityChallenge challenge = challengeOpt.get();
        List<ChallengeParticipation> participants = communityChallengeService.getChallengeParticipants(id);
        boolean isParticipating = communityChallengeService.isUserParticipating(user, id);
        
        model.addAttribute("challenge", challenge);
        model.addAttribute("participants", participants);
        model.addAttribute("isParticipating", isParticipating);
        model.addAttribute("participantCount", participants.size());
        
        return "community-challenge/detail";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        model.addAttribute("challenge", new CommunityChallenge());
        model.addAttribute("categories", categoryRepository.findAll());
        
        return "community-challenge/create";
    }

    @PostMapping("/create")
    public String createChallenge(@ModelAttribute CommunityChallenge challenge, 
                                 Principal principal, 
                                 RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        try {
            challenge.setCreator(user);
            CommunityChallenge savedChallenge = communityChallengeService.createChallenge(challenge);
            
            redirectAttributes.addFlashAttribute("success", "Community challenge created successfully!");
            return "redirect:/community-challenges/" + savedChallenge.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create challenge: " + e.getMessage());
            return "redirect:/community-challenges/create";
        }
    }

    @PostMapping("/{id}/join")
    public String joinChallenge(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        boolean success = communityChallengeService.joinChallenge(user, id);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Successfully joined the challenge!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to join challenge. You may already be participating or the challenge is full.");
        }
        
        return "redirect:/community-challenges/" + id;
    }

    @PostMapping("/{id}/leave")
    public String leaveChallenge(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        communityChallengeService.leaveChallenge(user, id);
        redirectAttributes.addFlashAttribute("success", "Successfully left the challenge.");
        
        return "redirect:/community-challenges/" + id;
    }

    @GetMapping("/my")
    public String myParticipations(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        
        List<ChallengeParticipation> participations = communityChallengeService.getUserParticipations(user);
        model.addAttribute("participations", participations);
        
        return "community-challenge/my-challenges";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        
        Optional<CommunityChallenge> challengeOpt = communityChallengeService.getChallengeById(id);
        if (challengeOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found");
        }
        
        CommunityChallenge challenge = challengeOpt.get();
        
        // Only creator can edit
        if (!challenge.getCreator().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit challenges you created");
        }
        
        model.addAttribute("challenge", challenge);
        model.addAttribute("categories", categoryRepository.findAll());
        
        return "community-challenge/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateChallenge(@PathVariable Long id, 
                                 @ModelAttribute CommunityChallenge updatedChallenge,
                                 Principal principal, 
                                 RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        Optional<CommunityChallenge> challengeOpt = communityChallengeService.getChallengeById(id);
        if (challengeOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found");
        }
        
        CommunityChallenge existingChallenge = challengeOpt.get();
        
        // Only creator can edit
        if (!existingChallenge.getCreator().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit challenges you created");
        }
        
        try {
            // Update allowed fields
            existingChallenge.setTitle(updatedChallenge.getTitle());
            existingChallenge.setDescription(updatedChallenge.getDescription());
            existingChallenge.setRules(updatedChallenge.getRules());
            existingChallenge.setScoringCriteria(updatedChallenge.getScoringCriteria());
            
            // Only allow date changes if challenge hasn't started
            if ("UPCOMING".equals(existingChallenge.getStatus())) {
                existingChallenge.setStartDate(updatedChallenge.getStartDate());
                existingChallenge.setEndDate(updatedChallenge.getEndDate());
            }
            
            communityChallengeService.updateChallenge(existingChallenge);
            
            redirectAttributes.addFlashAttribute("success", "Challenge updated successfully!");
            return "redirect:/community-challenges/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update challenge: " + e.getMessage());
            return "redirect:/community-challenges/" + id + "/edit";
        }
    }

    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
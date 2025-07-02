package com.healthyForum.controller.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.ChallengeTeam;
import com.healthyForum.model.challenge.TeamMember;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.service.challenge.TeamService;
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
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final UserRepository userRepository;

    public TeamController(TeamService teamService, UserRepository userRepository) {
        this.teamService = teamService;
        this.userRepository = userRepository;
    }

    @GetMapping("/challenge/{challengeId}")
    public String listTeams(@PathVariable Long challengeId, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        
        List<ChallengeTeam> allTeams = teamService.getTeamsForChallenge(challengeId);
        List<ChallengeTeam> availableTeams = teamService.getAvailableTeams(challengeId);
        Optional<ChallengeTeam> userTeam = teamService.getUserTeamForChallenge(user, challengeId);
        
        model.addAttribute("challengeId", challengeId);
        model.addAttribute("allTeams", allTeams);
        model.addAttribute("availableTeams", availableTeams);
        model.addAttribute("userTeam", userTeam.orElse(null));
        model.addAttribute("hasTeam", userTeam.isPresent());
        
        return "team/list";
    }

    @GetMapping("/{teamId}")
    public String viewTeam(@PathVariable Long teamId, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        
        Optional<ChallengeTeam> teamOpt = teamService.getTeamMembers(teamId)
                .stream()
                .findFirst()
                .map(TeamMember::getTeam);
        
        if (teamOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found");
        }
        
        ChallengeTeam team = teamOpt.get();
        List<TeamMember> members = teamService.getTeamMembers(teamId);
        boolean isMember = members.stream().anyMatch(m -> m.getUser().equals(user));
        boolean isCaptain = team.getCaptain().equals(user);
        
        model.addAttribute("team", team);
        model.addAttribute("members", members);
        model.addAttribute("isMember", isMember);
        model.addAttribute("isCaptain", isCaptain);
        
        return "team/detail";
    }

    @GetMapping("/create/{challengeId}")
    public String showCreateForm(@PathVariable Long challengeId, Model model, Principal principal) {
        getCurrentUser(principal); // Verify user is logged in
        
        model.addAttribute("challengeId", challengeId);
        model.addAttribute("team", new ChallengeTeam());
        
        return "team/create";
    }

    @PostMapping("/create/{challengeId}")
    public String createTeam(@PathVariable Long challengeId,
                            @RequestParam String teamName,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) Integer maxMembers,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        try {
            ChallengeTeam team = teamService.createTeam(user, challengeId, teamName, description, maxMembers);
            redirectAttributes.addFlashAttribute("success", "Team created successfully!");
            return "redirect:/teams/" + team.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create team: " + e.getMessage());
            return "redirect:/teams/create/" + challengeId;
        }
    }

    @PostMapping("/{teamId}/join")
    public String joinTeam(@PathVariable Long teamId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        boolean success = teamService.joinTeam(user, teamId);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Successfully joined the team!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to join team. The team may be full or you may already be in a team for this challenge.");
        }
        
        return "redirect:/teams/" + teamId;
    }

    @PostMapping("/{teamId}/leave")
    public String leaveTeam(@PathVariable Long teamId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        boolean success = teamService.leaveTeam(user, teamId);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Successfully left the team.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to leave team. Captains must disband the team instead of leaving.");
        }
        
        return "redirect:/teams/" + teamId;
    }

    @PostMapping("/{teamId}/disband")
    public String disbandTeam(@PathVariable Long teamId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        
        boolean success = teamService.disbandTeam(user, teamId);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Team disbanded successfully.");
            // Get challenge ID to redirect back to teams list
            Optional<ChallengeTeam> teamOpt = teamService.getTeamMembers(teamId)
                    .stream()
                    .findFirst()
                    .map(TeamMember::getTeam);
            
            if (teamOpt.isPresent()) {
                Long challengeId = teamOpt.get().getCommunityChallenge().getId();
                return "redirect:/teams/challenge/" + challengeId;
            }
            return "redirect:/community-challenges";
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to disband team. Only the team captain can disband the team.");
            return "redirect:/teams/" + teamId;
        }
    }

    @GetMapping("/my")
    public String myTeams(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        
        // This would need to be implemented in TeamService if needed
        // For now, redirect to community challenges where user can see their participations
        return "redirect:/community-challenges/my";
    }

    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
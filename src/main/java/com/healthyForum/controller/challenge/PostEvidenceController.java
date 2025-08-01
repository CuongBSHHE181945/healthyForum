package com.healthyForum.controller.challenge;

import com.healthyForum.model.*;
import com.healthyForum.model.Enum.EvidenceStatus;
import com.healthyForum.model.Enum.ReactionType;
import com.healthyForum.model.Enum.Visibility;
import com.healthyForum.model.Post.PostReaction;
import com.healthyForum.model.challenge.EvidencePost;
import com.healthyForum.model.challenge.*;
import com.healthyForum.model.Post.*;
import com.healthyForum.repository.Post.PostReactionRepository;
import com.healthyForum.repository.Post.PostRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.challenge.ChallengeRepository;
import com.healthyForum.repository.challenge.EvidencePostRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import com.healthyForum.service.UserService;
import com.healthyForum.service.challenge.ChallengeTrackingService;
import com.healthyForum.service.challenge.EvidenceService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/evidence")
public class PostEvidenceController {

    private final PostRepository postRepository;
    private final EvidencePostRepository evidencePostRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EvidenceService evidenceService;
    private final ChallengeTrackingService challengeTrackingService;
    private final PostReactionRepository postReactionRepository;

    public PostEvidenceController(PostRepository postRepository, EvidencePostRepository evidencePostRepository, ChallengeRepository challengeRepository, UserChallengeRepository userChallengeRepository, UserRepository userRepository, UserService userService, EvidenceService evidenceService, ChallengeTrackingService challengeTrackingService, PostReactionRepository postReactionRepository) {
        this.postRepository = postRepository;
        this.evidencePostRepository = evidencePostRepository;
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.evidenceService = evidenceService;
        this.challengeTrackingService = challengeTrackingService;
        this.postReactionRepository = postReactionRepository;
    }

    @GetMapping("/review/{userChallengeId}")
    public String viewEvidenceToApprove(@PathVariable Integer userChallengeId, Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
        List<EvidencePost> evidenceList = evidenceService.getAllEvidenceForSameChallenge(userChallengeId, user.getId());

        Map<Long, Long> likeCounts = new HashMap<>();
        Map<Long, Long> dislikeCounts = new HashMap<>();
        Map<Long, ReactionType> userReactions = new HashMap<>();

        for (EvidencePost evidence : evidenceList) {
            Long postId = evidence.getPost().getId();
            Post post = evidence.getPost();
            likeCounts.put(postId, postReactionRepository.countByPostAndType(post, ReactionType.LIKE));
            dislikeCounts.put(postId, postReactionRepository.countByPostAndType(post, ReactionType.DISLIKE));

            Optional<PostReaction> optionalReaction = postReactionRepository.findByUserAndPost(user, post);
            if (optionalReaction.isPresent()) {
                userReactions.put(postId, optionalReaction.get().getType());
            }
        }

        model.addAttribute("userChallengeId", userChallengeId);
        model.addAttribute("evidenceList", evidenceList);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("dislikeCounts", dislikeCounts);
        model.addAttribute("userReactions", userReactions);

        return "evidence/review_list";
    }

    @PostMapping("/react/{evidenceId}")
    public String approveEvidence(@PathVariable Integer evidenceId, @RequestParam String reactionType, Principal principal, Model model,
                                    @RequestParam Integer userChallengeId) {
        User user = userService.getCurrentUser(principal);
        EvidencePost post = evidencePostRepository.findById(evidenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        evidenceService.reactToEvidence(user, evidenceId, reactionType);
        return "redirect:/evidence/review/" + userChallengeId;
    }

    @GetMapping("/create")
    public String showCreateForm(@RequestParam Integer userChallengeId, Model model, Principal principal) {

        User user = userService.getCurrentUser(principal);
        UserChallenge userChallenge = userChallengeRepository.findById(userChallengeId)
                .orElseThrow(() -> new IllegalStateException("User has not joined this challenge."));

        int currentDay = challengeTrackingService.getProgress(userChallengeId).size() + 1;
        String challengeTitle = userChallenge.getChallenge().getName();

        String suggestedTitle = "Day " + currentDay + " – " + challengeTitle;

        Post post = new Post();
        post.setTitle(suggestedTitle);

        model.addAttribute("post", post);
        model.addAttribute("userChallenge", userChallenge);
        model.addAttribute("challengeName", challengeTitle);

        return "evidence/create"; // Thymeleaf template
    }

    @PostMapping("/create")
    public String submitPostEvidence(@ModelAttribute("post") Post post,
                                     @RequestParam(required = false) MultipartFile imageFile,
                                     @RequestParam(required = false) Integer userChallengeId,
                                     // @RequestParam(required = false, defaultValue = "true") boolean markAsEvidence,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) throws IOException {
        User user = userService.getCurrentUser(principal);
        // Fill post
        post.setUser(user);
        post.setDraft(false);
        post.setVisibility(Visibility.PUBLIC);
        post.setBanned(false);

        // Save image
        if (imageFile != null && !imageFile.isEmpty()) {
            String url = evidenceService.saveEvidenceImage(imageFile, user.getUsername());
            post.setImageUrl(url);
        }

        postRepository.save(post);


            UserChallenge userChallenge = userChallengeRepository.findById(userChallengeId)
                    .orElseThrow(() -> new IllegalStateException("User has not joined this challenge."));

            EvidencePost evidence = new EvidencePost();
            evidence.setPost(post);
            evidence.setUserChallenge(userChallenge);
            evidence.setStatus(EvidenceStatus.PENDING);
            evidence.setVoteBased(true);
            evidence.setVoteTimeout(LocalDateTime.now().plusHours(24));
            evidence.setFallbackToAdmin(false);
            evidence.setCreatedAt(LocalDateTime.now());

            evidencePostRepository.save(evidence);


        redirectAttributes.addFlashAttribute("successMessage", "Evidence submitted!");
        return "redirect:/challenge/progress/" + userChallengeId;
    }

    @GetMapping("/edit/{evidenceId}")
    public String showEditForm(@PathVariable Integer evidenceId, Model model, Principal principal) {
        EvidencePost evidence = evidencePostRepository.findById(evidenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!evidence.getPost().getUser().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        model.addAttribute("userChallenge", evidence.getUserChallenge());
        model.addAttribute("evidenceId", evidenceId);
        model.addAttribute("post", evidence.getPost());
        return "evidence/edit";
    }

    @PostMapping("/edit")
    public String updateEvidencePost(@RequestParam("evidenceId") Integer evidenceId,
                                     @ModelAttribute("post") Post updatedPost,
                                     @RequestParam(required = false) MultipartFile imageFile,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) throws IOException {
        EvidencePost evidence = evidencePostRepository.findById(evidenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!evidence.getPost().getUser().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Post post = evidence.getPost();
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());

        if (imageFile != null && !imageFile.isEmpty()) {
            String newUrl = evidenceService.saveEvidenceImage(imageFile, principal.getName());
            post.setImageUrl(newUrl);
        }

        postRepository.save(post);

        redirectAttributes.addFlashAttribute("success", "Evidence updated.");
        return "redirect:/challenge/progress/" + evidence.getUserChallenge().getId();
    }

    @GetMapping("/retry/{userChallengeId}")
    public String showRetryEvidenceForm(@PathVariable Integer userChallengeId, Model model, Principal principal,
                                        RedirectAttributes redirectAttributes) {
        EvidencePost todayEvidence = evidencePostRepository.findByUserChallengeIdAndPostDate(userChallengeId, LocalDate.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        EvidenceStatus status = todayEvidence.getStatus();

        switch (status) {
            case APPROVED, PENDING, UNDER_REVIEW:
                // Already submitted → redirect to edit
                return "redirect:/evidence/edit/" + todayEvidence.getPost().getId();

            case REJECTED:
                // Retry → pass note to user
                redirectAttributes.addFlashAttribute("retrying", true);
                redirectAttributes.addFlashAttribute("rejectedReason", "You got too many dislike");
                break;

            default:
                // fallback (optional)
                return "redirect:/evidence/create?userChallengeId=" + userChallengeId;
        }

        return "redirect:/evidence/create?userChallengeId=" + userChallengeId;
    }
}


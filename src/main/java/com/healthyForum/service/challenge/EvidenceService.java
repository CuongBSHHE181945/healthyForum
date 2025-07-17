package com.healthyForum.service.challenge;

import com.healthyForum.model.Enum.EvidenceStatus;
import com.healthyForum.model.Enum.ReactionType;
import com.healthyForum.model.EvidencePost;
import com.healthyForum.model.User;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.model.challenge.UserChallengeProgress;
import com.healthyForum.repository.challenge.EvidencePostRepository;
import com.healthyForum.repository.challenge.EvidenceReactionRepository;
import com.healthyForum.repository.challenge.UserChallengeProgressRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EvidenceService {
    private final UserChallengeRepository userChallengeRepository;
    private final EvidencePostRepository evidencePostRepository;
    private final ReactionService reactionService;
    private final UserChallengeProgressRepository userChallengeProgressRepository;
    private final EvidenceReactionRepository evidenceReactionRepository;

    public EvidenceService(UserChallengeRepository userChallengeRepository, EvidencePostRepository evidencePostRepository, ReactionService reactionService, UserChallengeProgressRepository userChallengeProgressRepository, EvidenceReactionRepository evidenceReactionRepository) {
        this.userChallengeRepository = userChallengeRepository;
        this.evidencePostRepository = evidencePostRepository;
        this.reactionService = reactionService;
        this.userChallengeProgressRepository = userChallengeProgressRepository;
        this.evidenceReactionRepository = evidenceReactionRepository;
    }

    public String saveEvidenceImage(MultipartFile file, String username) throws IOException {
        String evidenceDir = new File("src/main/resources/static/uploads/evidences/").getAbsolutePath();
        new File(evidenceDir).mkdirs();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");
        String timestamp = java.time.LocalDateTime.now().format(formatter);
        String evidenceFileName = username + "_" + timestamp + "_" + file.getOriginalFilename();

        File evidenceFile = new File(evidenceDir + "/" + evidenceFileName);
        file.transferTo(evidenceFile);
        return "/uploads/evidences/" + evidenceFileName;
    }

    public List<EvidencePost> getAllEvidenceForSameChallenge(Integer userChallengeId, Long currentUserId) {
        UserChallenge baseUC = userChallengeRepository.findById(userChallengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        int challengeId = baseUC.getChallenge().getId();

        List<UserChallenge> allUC = userChallengeRepository.findAllByChallengeId(challengeId);
        List<Integer> allUCIds = allUC.stream()
                .filter(uc -> !uc.getUser().getId().equals(currentUserId)) // exclude current user
                .map(UserChallenge::getId)
                .collect(Collectors.toList());

        return evidencePostRepository.findAllByUserChallengeIds(allUCIds);
    }

    public void reactToEvidence(User user, Integer evidenceId, String reactionType) {
        EvidencePost post = evidencePostRepository.findById(evidenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Long postId = post.getPost().getId();

        // Save or update user reaction (LIKE or DISLIKE)
        reactionService.saveOrUpdateReaction(user, evidenceId, reactionType);

        // Recalculate total reactions
        long likeCount = evidenceReactionRepository.countByPostIdAndReactionType(postId, ReactionType.LIKE);
        long dislikeCount = evidenceReactionRepository.countByPostIdAndReactionType(postId, ReactionType.DISLIKE);

        EvidenceStatus currentStatus = post.getStatus();
        // Evaluate status based on current counts
        if (currentStatus == EvidenceStatus.PENDING) {
            if (likeCount >= 3 && (likeCount >= (dislikeCount*3))) {
                post.setStatus(EvidenceStatus.APPROVED);
                autoTickProgress(post); // ✅ add this line
            } else if (dislikeCount + likeCount >= 4) {
                post.setStatus(EvidenceStatus.REJECTED);
            }
        } else if (currentStatus == EvidenceStatus.APPROVED && dislikeCount > likeCount) {
            post.setStatus(EvidenceStatus.UNDER_REVIEW); // status downgrade
        }

        evidencePostRepository.save(post);
    }

    public void autoTickProgress(EvidencePost post) {
        UserChallenge uc = post.getUserChallenge();
        LocalDate today = post.getCreatedAt().toLocalDate();

        boolean alreadyLogged = userChallengeProgressRepository.existsByUserChallengeIdAndDate(uc.getId(), today);
        if (!alreadyLogged) {
            UserChallengeProgress progress = new UserChallengeProgress();
            progress.setUserChallenge(uc);
            progress.setDate(today);
            progress.setCompleted(true);
            userChallengeProgressRepository.save(progress);
        }
    }

}

package com.healthyForum.service.challenge;

import com.healthyForum.model.Enum.EvidenceStatus;
import com.healthyForum.model.Enum.ReactionType;
import com.healthyForum.model.Post.Post;
import com.healthyForum.model.challenge.EvidencePost;
import com.healthyForum.model.User;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.model.challenge.UserChallengeProgress;
import com.healthyForum.repository.Post.PostReactionRepository;
import com.healthyForum.repository.challenge.EvidencePostRepository;
import com.healthyForum.repository.challenge.UserChallengeProgressRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EvidenceService {
    private final UserChallengeRepository userChallengeRepository;
    private final EvidencePostRepository evidencePostRepository;
    private final ReactionService reactionService;
    private final UserChallengeProgressRepository userChallengeProgressRepository;
    private final PostReactionRepository postReactionRepository;

    public EvidenceService(UserChallengeRepository userChallengeRepository, EvidencePostRepository evidencePostRepository, ReactionService reactionService, UserChallengeProgressRepository userChallengeProgressRepository,PostReactionRepository postReactionRepository) {
        this.userChallengeRepository = userChallengeRepository;
        this.evidencePostRepository = evidencePostRepository;
        this.reactionService = reactionService;
        this.userChallengeProgressRepository = userChallengeProgressRepository;
        this.postReactionRepository = postReactionRepository;
    }

    public String saveEvidenceImage(MultipartFile file, String username) throws IOException {
            try {
                if (file == null || file.isEmpty()) {
                    throw new IllegalArgumentException("File is empty or null");
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");
                String timestamp = LocalDateTime.now().format(formatter);

                String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();
                String evidenceFileName = username + "_" + timestamp + "_" + originalFileName;

                // Path relative to the project root
                Path projectRoot = Paths.get("").toAbsolutePath(); // current working dir
                Path evidenceDir = projectRoot.resolve("uploads").resolve("evidences");
                Files.createDirectories(evidenceDir); // create folders if they don't exist

                Path targetFile = evidenceDir.resolve(evidenceFileName);
                file.transferTo(targetFile.toFile());

                // Return relative path for access via web
                return "/uploads/evidences/" + evidenceFileName;

            } catch (IOException e) {
                throw new RuntimeException("Failed to save file", e);
            }
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
        EvidencePost evidencePost = evidencePostRepository.findById(evidenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Long postId = evidencePost.getPost().getId();
        Post post = evidencePost.getPost();

        // Save or update user reaction (LIKE or DISLIKE)
        reactionService.saveOrUpdateReaction(user, evidenceId, reactionType);

        // Recalculate total reactions
        long likeCount = postReactionRepository.countByPostAndType(post, ReactionType.LIKE);
        long dislikeCount = postReactionRepository.countByPostAndType(post, ReactionType.DISLIKE);

        EvidenceStatus currentStatus = evidencePost.getStatus();
        // Evaluate status based on current counts
        if (currentStatus == EvidenceStatus.PENDING) {
            if (likeCount >= 3 && (likeCount >= (dislikeCount*3))) {
                evidencePost.setStatus(EvidenceStatus.APPROVED);
                autoTickProgress(evidencePost); // ✅ add this line
            } else if (dislikeCount + likeCount >= 4) {
                rejectEvidence(evidencePost);
                return;
            }
        } else if (currentStatus == EvidenceStatus.APPROVED && dislikeCount > likeCount) {
            evidencePost.setStatus(EvidenceStatus.UNDER_REVIEW); // status downgrade
        }

        evidencePostRepository.save(evidencePost);
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

    public Optional<EvidencePost> getTodaysEvidence(Integer userChallengeId) {
        return evidencePostRepository.findByUserChallengeIdAndPostDate(userChallengeId, LocalDate.now());
    }

    public Optional<EvidencePost> getLatestEvidence(Integer userChallengeId) {
        return evidencePostRepository
                .findTopByUserChallengeIdOrderByCreatedAtDesc(userChallengeId);
    }

    public boolean isEvidence(Post post){
        Optional<EvidencePost> evidenceOpt = evidencePostRepository.findByPost(post);
        return evidenceOpt.isPresent();
    }

    public void rejectEvidence(EvidencePost evidencePost){
        evidencePost.setStatus(EvidenceStatus.REJECTED);
        evidencePostRepository.save(evidencePost);
    }

    public EvidencePost findByPost(Post post){
        Optional<EvidencePost> evidenceOpt = evidencePostRepository.findByPost(post);
        return evidenceOpt.orElse(null);
    }
}

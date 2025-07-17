package com.healthyForum.service.challenge;

import com.healthyForum.model.Enum.ReactionType;
import com.healthyForum.model.EvidencePost;
import com.healthyForum.model.User;
import com.healthyForum.model.challenge.EvidenceReaction;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.challenge.EvidencePostRepository;
import com.healthyForum.repository.challenge.EvidenceReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ReactionService {

    @Autowired
    private EvidenceReactionRepository reactionRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EvidencePostRepository evidencePostRepository;

    /**
     * Creates or updates a user's reaction (like or dislike) for an evidence post.
     */
    public void saveOrUpdateReaction(User user, Integer evidenceId, String type) {

        EvidencePost post = evidencePostRepository.findById(evidenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ReactionType reactionType = ReactionType.valueOf(type.toUpperCase());

        Optional<EvidenceReaction> existing = reactionRepo.findByUserIdAndEvidencePostId(user.getId(), evidenceId);

        if (existing.isPresent()) {
            EvidenceReaction reaction = existing.get();
            if (reaction.getReactionType() != reactionType) {
                reaction.setReactionType(reactionType);
                reactionRepo.save(reaction);
            }
        } else {
            EvidenceReaction reaction = new EvidenceReaction();
            reaction.setUser(user);
            reaction.setEvidencePost(post);
            reaction.setReactionType(reactionType);
            reactionRepo.save(reaction);
        }
    }

    public long countReactionsByType(Long postId, ReactionType type) {
        return reactionRepo.countByPostIdAndReactionType(postId, type);
    }

    public EvidenceReaction getUserReaction(Long postId, Long userId) {
        return reactionRepo.findByPostIdAndUserId(postId, userId).orElse(null);
    }
}


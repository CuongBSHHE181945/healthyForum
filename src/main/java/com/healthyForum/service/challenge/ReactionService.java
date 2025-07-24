package com.healthyForum.service.challenge;

import com.healthyForum.model.Enum.ReactionType;
import com.healthyForum.model.Post.Post;
import com.healthyForum.model.challenge.EvidencePost;
import com.healthyForum.model.User;
import com.healthyForum.model.Post.PostReaction;
import com.healthyForum.repository.Post.PostReactionRepository;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.challenge.EvidencePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ReactionService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EvidencePostRepository evidencePostRepository;

    @Autowired
    private PostReactionRepository postReactionRepository;

    /**
     * Creates or updates a user's reaction (like or dislike) for an evidence post.
     */
    public void saveOrUpdateReaction(User user, Integer evidenceId, String type) {
        EvidencePost evidencePost = evidencePostRepository.findById(evidenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Post post = evidencePost.getPost(); // make sure this mapping exists
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found for this evidence.");
        }

        ReactionType reactionType = ReactionType.valueOf(type.toUpperCase());

        Optional<PostReaction> existing = postReactionRepository.findByUserAndPost(user, post);

        if (existing.isPresent()) {
            PostReaction reaction = existing.get();
            if (reaction.getType() != reactionType) {
                reaction.setType(reactionType);
                postReactionRepository.save(reaction);
            }
        } else {
            PostReaction reaction = new PostReaction();
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setType(reactionType);
            postReactionRepository.save(reaction);
        }
    }
}


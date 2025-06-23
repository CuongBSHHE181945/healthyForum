package com.healthyForum.service.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.Challenge;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.repository.badge.UserBadgeRepository;
import com.healthyForum.repository.challenge.ChallengeRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepo;
    private final UserChallengeRepository userChallengeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public ChallengeService(ChallengeRepository challengeRepo, UserChallengeRepository userChallengeRepository, UserBadgeRepository userBadgeRepository) {
        this.challengeRepo = challengeRepo;
        this.userChallengeRepository = userChallengeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    public List<Challenge> getAllChallenges() {
        return challengeRepo.findAll();
    }

    public Optional<Challenge> getChallengeById(int id) {
        return challengeRepo.findById(id);
    }

    public void joinChallenge(User user, int challengeId) {
        Optional<UserChallenge> existing = userChallengeRepository.findTopByUserAndChallengeIdOrderByJoinDateDesc(user, challengeId);

        Challenge challenge = challengeRepo.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));

        if (existing.isPresent() && existing.get().getStatus().equals("ACTIVE")) {
            // User already has an active challenge of this type → don't rejoin
            return;
        }

            UserChallenge uc = new UserChallenge();
            uc.setUser(user);
            uc.setChallenge(challenge);
            uc.setJoinDate(LocalDate.now());
            uc.setStatus("ACTIVE");
            uc.setLastCheckDate(LocalDate.now());
            userChallengeRepository.save(uc);

    }

    public List<UserChallenge> getActiveChallengesForUser(User user) {
        return userChallengeRepository.findByUserAndStatus(user, "ACTIVE");
    }

    public List<Integer> getJoinedChallengeIds(User user) {
        List<UserChallenge> userChallenges = userChallengeRepository.findByUserAndStatus(user, "ACTIVE");
        List<Integer> challengeIds = new ArrayList<>();
        for (UserChallenge uc : userChallenges) {
            challengeIds.add(uc.getChallenge().getId());
        }
        return challengeIds;
    }

    public boolean hasUserEarnedBadge(Long userId, int challengeId) {
        return userBadgeRepository.existsByIdUserIdAndIdBadgeId(userId, challengeId);
    }

    public List<Integer> getBadgeEarnedChallengeIds(User user) {
        return userBadgeRepository.findChallengeIdsByUserId(user.getUserID());
    }
}

package com.healthyForum.service.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.badge.*;
import com.healthyForum.model.challenge.Challenge;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.repository.badge.BadgeRepository;
import com.healthyForum.repository.badge.BadgeRequirementRepository;
import com.healthyForum.repository.badge.BadgeSourceTypeRepository;
import com.healthyForum.repository.badge.UserBadgeRepository;
import com.healthyForum.repository.challenge.ChallengeRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import com.healthyForum.service.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepo;
    private final UserChallengeRepository userChallengeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final BadgeSourceTypeRepository badgeSourceTypeRepository;
    private final UserServiceImpl userService;
    private final BadgeRequirementRepository badgeRequirementRepository;

    public ChallengeService(ChallengeRepository challengeRepo, UserChallengeRepository userChallengeRepository, UserBadgeRepository userBadgeRepository, BadgeRepository badgeRepository, BadgeSourceTypeRepository badgeSourceTypeRepository, UserServiceImpl userService, BadgeRequirementRepository badgeRequirementRepository) {
        this.challengeRepo = challengeRepo;
        this.userChallengeRepository = userChallengeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
        this.badgeSourceTypeRepository = badgeSourceTypeRepository;
        this.userService = userService;
        this.badgeRequirementRepository = badgeRequirementRepository;
    }

    public List<Challenge> getAllChallenges() {
        return challengeRepo.findAll();
    }

    public List<Challenge> getAllPersonalChallenges() {
        return challengeRepo.findByType_Id(1); // 1 = Personal
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
        return userChallengeRepository.findByUser(user);
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
        return userBadgeRepository.findChallengeIdsByUserId(user.getId());
    }

    public void createChallengeWithBadge(Challenge challenge, Badge badge) {
        // 1. Save the challenge
        Challenge savedChallenge = challengeRepo.save(challenge);

        // 2. Save the badge
        Badge savedBadge = badgeRepository.save(badge);
        BadgeSourceType personal = badgeSourceTypeRepository.findById(1)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));;
        // 3. Link badge to challenge
        BadgeRequirement requirement = new BadgeRequirement();
        requirement.setBadge(savedBadge);
        requirement.setSourceType(personal); // Source = CHALLENGE
        requirement.setSourceId(savedChallenge.getId());

        badgeRequirementRepository.save(requirement);
    }

    public Challenge getChallengeForEdit(Integer challengeId, User currentUser) {
        Challenge challenge = challengeRepo.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        boolean isOwner = challenge.getCreator().getId().equals(currentUser.getId());
        if (!isOwner && !userService.isAdminOrModerator(currentUser.getRole())) {
            throw new AccessDeniedException("You cannot edit this challenge.");
        }
        return challenge;
    }

    public boolean isChallengeInUse(int challengeId) {
        return userChallengeRepository.existsByChallengeId(challengeId);
    }

    public void updateChallenge(Challenge updated, User user) {
        updated.setCreator(user);
        boolean inUse = isChallengeInUse(updated.getId());
        if (inUse) {
            // Prevent updating critical fields
            Challenge existing = challengeRepo.findById(updated.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

            // Preserve restricted fields
            updated.setType(existing.getType());
            updated.setCategory(existing.getCategory());
            updated.setCreator(existing.getCreator());
        }

        challengeRepo.save(updated);
    }
    public void deleteChallenge(int id) {
        challengeRepo.deleteById(id);
    }
}

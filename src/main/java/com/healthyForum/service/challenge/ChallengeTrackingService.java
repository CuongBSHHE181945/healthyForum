package com.healthyForum.service.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.UserChallenge;
import com.healthyForum.model.challenge.UserChallengeProgress;
import com.healthyForum.repository.challenge.UserChallengeProgressRepository;
import com.healthyForum.repository.challenge.UserChallengeRepository;
import com.healthyForum.service.badge.BadgeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeTrackingService {

    private final UserChallengeRepository userChallengeRepo;
    private final UserChallengeProgressRepository progressRepo;
    private final BadgeService badgeService;

    public ChallengeTrackingService(UserChallengeRepository userChallengeRepo, UserChallengeProgressRepository progressRepo, BadgeService badgeService) {
        this.userChallengeRepo = userChallengeRepo;
        this.progressRepo = progressRepo;
        this.badgeService = badgeService;
    }

    public boolean tickProgress(int userChallengeId) {
        UserChallenge userChallenge = userChallengeRepo.findById(userChallengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        LocalDate today = LocalDate.now();
        boolean alreadyTicked = progressRepo.existsByUserChallengeIdAndDate(userChallengeId, today);
        if (alreadyTicked) return false;

        //Save progress
        UserChallengeProgress progress = new UserChallengeProgress();
        progress.setUserChallenge(userChallenge);
        progress.setDate(today);
        progress.setCompleted(true);
        progressRepo.save(progress);

        return true;
    }

    public List<UserChallengeProgress> getProgress(int userChallengeId) {
        return progressRepo.findByUserChallengeIdOrderByDateAsc(userChallengeId);
    }

    public int getStreak(int userChallengeId) {
        List<UserChallengeProgress> logs = getProgress(userChallengeId);
        int streak = 0;
        LocalDate today = LocalDate.now();

        for (int i = logs.size() - 1; i >= 0; i--) {
            LocalDate date = logs.get(i).getDate();
            if (date.equals(today.minusDays(streak))) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    public boolean checkAndCompleteIfDone(int userChallengeId) {
        UserChallenge userChallenge = userChallengeRepo.findById(userChallengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        int progressCount = progressRepo.findByUserChallengeIdOrderByDateAsc(userChallengeId).size();
        int requiredDays = userChallenge.getChallenge().getDurationDays();

        if (progressCount >= requiredDays) {
            userChallenge.setStatus("COMPLETED");
            userChallengeRepo.save(userChallenge);

            //Check if badge linked to this challenge and award badge
            badgeService.checkAndAwardBadge(userChallenge.getUser().getId(), userChallenge.getChallenge().getId());

            return true;
        }
        return false;
    }

}


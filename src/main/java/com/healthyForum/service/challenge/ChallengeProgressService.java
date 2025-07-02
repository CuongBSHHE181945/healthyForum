package com.healthyForum.service.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.*;
import com.healthyForum.repository.challenge.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChallengeProgressService {

    private final ChallengeActivityRepository activityRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final CommunityChallengeRepository challengeRepository;
    private final LeaderboardService leaderboardService;

    public ChallengeProgressService(ChallengeActivityRepository activityRepository,
                                  ChallengeParticipationRepository participationRepository,
                                  CommunityChallengeRepository challengeRepository,
                                  LeaderboardService leaderboardService) {
        this.activityRepository = activityRepository;
        this.participationRepository = participationRepository;
        this.challengeRepository = challengeRepository;
        this.leaderboardService = leaderboardService;
    }

    public ChallengeActivity logActivity(User user, Long challengeId, String activityType, 
                                       String description, String photoUrl, Integer pointsEarned) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            throw new IllegalArgumentException("Challenge not found");
        }

        CommunityChallenge challenge = challengeOpt.get();
        
        // Verify user is participating in the challenge
        if (!participationRepository.existsByCommunityChallengeAndUserAndStatus(challenge, user, "ACTIVE")) {
            throw new IllegalArgumentException("User is not participating in this challenge");
        }

        ChallengeActivity activity = new ChallengeActivity();
        activity.setCommunityChallenge(challenge);
        activity.setUser(user);
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setPhotoUrl(photoUrl);
        activity.setPointsEarned(pointsEarned != null ? pointsEarned : 0);
        activity.setActivityDate(LocalDateTime.now());
        activity.setVerified(true); // Auto-verify for now, could add manual verification later

        ChallengeActivity savedActivity = activityRepository.save(activity);

        // Update user's score
        if (pointsEarned != null && pointsEarned > 0) {
            updateUserScore(user, challenge, pointsEarned);
        }

        return savedActivity;
    }

    public ChallengeActivity uploadProgressPhoto(User user, Long challengeId, String photoUrl, String description) {
        return logActivity(user, challengeId, "PHOTO_UPLOAD", description, photoUrl, 10); // 10 points for photo upload
    }

    public ChallengeActivity addProgressUpdate(User user, Long challengeId, String description, Integer pointsEarned) {
        return logActivity(user, challengeId, "PROGRESS_UPDATE", description, null, pointsEarned);
    }

    public ChallengeActivity addComment(User user, Long challengeId, String comment) {
        return logActivity(user, challengeId, "COMMENT", comment, null, 1); // 1 point for engagement
    }

    public ChallengeActivity addCheer(User user, Long challengeId, String cheerMessage) {
        return logActivity(user, challengeId, "CHEER", cheerMessage, null, 2); // 2 points for cheering others
    }

    public List<ChallengeActivity> getChallengeActivities(Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            return activityRepository.findRecentActivities(challengeOpt.get());
        }
        return List.of();
    }

    public List<ChallengeActivity> getUserActivities(User user, Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            return activityRepository.findByCommunityChallengeAndUser(challengeOpt.get(), user);
        }
        return List.of();
    }

    public List<ChallengeActivity> getRecentActivities(Long challengeId, int daysBack) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            LocalDateTime startDate = LocalDateTime.now().minusDays(daysBack);
            return activityRepository.findActivitiesSince(challengeOpt.get(), startDate);
        }
        return List.of();
    }

    public void likeActivity(Long activityId) {
        Optional<ChallengeActivity> activityOpt = activityRepository.findById(activityId);
        if (activityOpt.isPresent()) {
            ChallengeActivity activity = activityOpt.get();
            activity.setLikesCount(activity.getLikesCount() + 1);
            activityRepository.save(activity);
        }
    }

    public void addCommentToActivity(Long activityId) {
        Optional<ChallengeActivity> activityOpt = activityRepository.findById(activityId);
        if (activityOpt.isPresent()) {
            ChallengeActivity activity = activityOpt.get();
            activity.setCommentsCount(activity.getCommentsCount() + 1);
            activityRepository.save(activity);
        }
    }

    public Integer getUserTotalPoints(User user, Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            Integer points = activityRepository.getTotalPointsForUser(challengeOpt.get(), user);
            return points != null ? points : 0;
        }
        return 0;
    }

    public Double calculateProgressPercentage(User user, Long challengeId) {
        // This is a basic calculation - can be customized based on challenge type
        Integer totalPoints = getUserTotalPoints(user, challengeId);
        
        // Assume 100 points = 100% completion (can be customized per challenge)
        double percentage = Math.min(100.0, (totalPoints / 100.0) * 100);
        
        // Update participation record
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            Optional<ChallengeParticipation> participationOpt = 
                participationRepository.findByCommunityChallengeAndUser(challengeOpt.get(), user);
            if (participationOpt.isPresent()) {
                ChallengeParticipation participation = participationOpt.get();
                participation.setProgressPercentage(percentage);
                
                // Mark as completed if 100%
                if (percentage >= 100.0 && !"COMPLETED".equals(participation.getStatus())) {
                    participation.setStatus("COMPLETED");
                    participation.setCompletedAt(LocalDateTime.now());
                }
                
                participationRepository.save(participation);
            }
        }
        
        return percentage;
    }

    private void updateUserScore(User user, CommunityChallenge challenge, Integer additionalPoints) {
        Optional<ChallengeParticipation> participationOpt = 
            participationRepository.findByCommunityChallengeAndUser(challenge, user);
        
        if (participationOpt.isPresent()) {
            ChallengeParticipation participation = participationOpt.get();
            Integer currentScore = participation.getIndividualScore();
            Integer newScore = currentScore + additionalPoints;
            
            participation.setIndividualScore(newScore);
            participationRepository.save(participation);
            
            // Update progress percentage
            calculateProgressPercentage(user, challenge.getId());
            
            // Refresh leaderboards
            leaderboardService.updateParticipantScore(challenge.getId(), user.getUserID(), newScore);
        }
    }

    public void verifyActivity(Long activityId, boolean verified) {
        Optional<ChallengeActivity> activityOpt = activityRepository.findById(activityId);
        if (activityOpt.isPresent()) {
            ChallengeActivity activity = activityOpt.get();
            boolean wasVerified = activity.getVerified();
            activity.setVerified(verified);
            activityRepository.save(activity);
            
            // If verification status changed, update scores
            if (wasVerified != verified) {
                Integer pointsChange = verified ? activity.getPointsEarned() : -activity.getPointsEarned();
                updateUserScore(activity.getUser(), activity.getCommunityChallenge(), pointsChange);
            }
        }
    }
}
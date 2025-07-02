package com.healthyForum.service.challenge;

import com.healthyForum.model.challenge.*;
import com.healthyForum.repository.challenge.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeaderboardService {

    private final LeaderboardEntryRepository leaderboardRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final ChallengeTeamRepository teamRepository;
    private final CommunityChallengeRepository challengeRepository;

    public LeaderboardService(LeaderboardEntryRepository leaderboardRepository,
                             ChallengeParticipationRepository participationRepository,
                             ChallengeTeamRepository teamRepository,
                             CommunityChallengeRepository challengeRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.participationRepository = participationRepository;
        this.teamRepository = teamRepository;
        this.challengeRepository = challengeRepository;
    }

    public List<LeaderboardEntry> getIndividualLeaderboard(Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            return leaderboardRepository.findByCommunityChallengeAndEntryTypeOrderByRankPosition(
                challengeOpt.get(), "INDIVIDUAL");
        }
        return List.of();
    }

    public List<LeaderboardEntry> getTeamLeaderboard(Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            return leaderboardRepository.findByCommunityChallengeAndEntryTypeOrderByRankPosition(
                challengeOpt.get(), "TEAM");
        }
        return List.of();
    }

    public void updateIndividualLeaderboard(Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            return;
        }

        CommunityChallenge challenge = challengeOpt.get();
        
        // Clear existing individual leaderboard entries
        leaderboardRepository.deleteByCommunityChallengeAndEntryType(challenge, "INDIVIDUAL");

        // Get all active participants ordered by score
        List<ChallengeParticipation> participants = 
            participationRepository.findActiveParticipantsOrderedByScore(challenge);

        // Create new leaderboard entries
        int rank = 1;
        int previousScore = -1;
        int actualRank = 1;
        
        for (ChallengeParticipation participation : participants) {
            if (participation.getIndividualScore() != previousScore) {
                rank = actualRank;
                previousScore = participation.getIndividualScore();
            }

            LeaderboardEntry entry = new LeaderboardEntry();
            entry.setCommunityChallenge(challenge);
            entry.setUser(participation.getUser());
            entry.setScore(participation.getIndividualScore());
            entry.setRankPosition(rank);
            entry.setEntryType("INDIVIDUAL");
            entry.setLastUpdated(LocalDateTime.now());

            leaderboardRepository.save(entry);
            actualRank++;
        }
    }

    public void updateTeamLeaderboard(Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty() || !challengeOpt.get().getIsTeamBased()) {
            return;
        }

        CommunityChallenge challenge = challengeOpt.get();
        
        // Clear existing team leaderboard entries
        leaderboardRepository.deleteByCommunityChallengeAndEntryType(challenge, "TEAM");

        // Get all teams ordered by score
        List<ChallengeTeam> teams = teamRepository.findTeamsOrderedByScore(challenge);

        // Create new leaderboard entries
        int rank = 1;
        int previousScore = -1;
        int actualRank = 1;
        
        for (ChallengeTeam team : teams) {
            if (!team.getTotalScore().equals(previousScore)) {
                rank = actualRank;
                previousScore = team.getTotalScore();
            }

            LeaderboardEntry entry = new LeaderboardEntry();
            entry.setCommunityChallenge(challenge);
            entry.setTeam(team);
            entry.setScore(team.getTotalScore());
            entry.setRankPosition(rank);
            entry.setEntryType("TEAM");
            entry.setLastUpdated(LocalDateTime.now());

            leaderboardRepository.save(entry);
            actualRank++;
        }
    }

    public void updateAllLeaderboards(Long challengeId) {
        updateIndividualLeaderboard(challengeId);
        
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent() && challengeOpt.get().getIsTeamBased()) {
            updateTeamLeaderboard(challengeId);
        }
    }

    public void updateParticipantScore(Long challengeId, Long userId, Integer newScore) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            return;
        }

        CommunityChallenge challenge = challengeOpt.get();
        
        // Find and update participation
        List<ChallengeParticipation> participations = participationRepository.findByCommunityChallenge(challenge);
        Optional<ChallengeParticipation> userParticipation = participations.stream()
            .filter(p -> p.getUser().getUserID().equals(userId))
            .findFirst();

        if (userParticipation.isPresent()) {
            ChallengeParticipation participation = userParticipation.get();
            participation.setIndividualScore(newScore);
            participationRepository.save(participation);

            // Update team score if this is a team challenge
            if (challenge.getIsTeamBased() && participation.getTeam() != null) {
                updateTeamTotalScore(participation.getTeam());
            }

            // Refresh leaderboards
            updateAllLeaderboards(challengeId);
        }
    }

    private void updateTeamTotalScore(ChallengeTeam team) {
        List<ChallengeParticipation> teamParticipations = 
            participationRepository.findByCommunityChallenge(team.getCommunityChallenge());
        
        int totalScore = teamParticipations.stream()
            .filter(p -> team.equals(p.getTeam()) && "ACTIVE".equals(p.getStatus()))
            .mapToInt(ChallengeParticipation::getIndividualScore)
            .sum();

        team.setTotalScore(totalScore);
        teamRepository.save(team);
    }

    public Integer getUserRank(Long challengeId, Long userId) {
        List<LeaderboardEntry> leaderboard = getIndividualLeaderboard(challengeId);
        return leaderboard.stream()
            .filter(entry -> entry.getUser() != null && entry.getUser().getUserID().equals(userId))
            .map(LeaderboardEntry::getRankPosition)
            .findFirst()
            .orElse(null);
    }

    public Integer getTeamRank(Long challengeId, Long teamId) {
        List<LeaderboardEntry> leaderboard = getTeamLeaderboard(challengeId);
        return leaderboard.stream()
            .filter(entry -> entry.getTeam() != null && entry.getTeam().getId().equals(teamId))
            .map(LeaderboardEntry::getRankPosition)
            .findFirst()
            .orElse(null);
    }
}
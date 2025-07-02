package com.healthyForum.service.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.CommunityChallenge;
import com.healthyForum.model.challenge.ChallengeParticipation;
import com.healthyForum.repository.challenge.CommunityChallengeRepository;
import com.healthyForum.repository.challenge.ChallengeParticipationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommunityChallengeService {

    private final CommunityChallengeRepository communityChallengeRepository;
    private final ChallengeParticipationRepository participationRepository;

    public CommunityChallengeService(CommunityChallengeRepository communityChallengeRepository,
                                   ChallengeParticipationRepository participationRepository) {
        this.communityChallengeRepository = communityChallengeRepository;
        this.participationRepository = participationRepository;
    }

    public List<CommunityChallenge> getAllActiveChallenges() {
        return communityChallengeRepository.findActivePublicChallenges(LocalDate.now());
    }

    public List<CommunityChallenge> getAllUpcomingChallenges() {
        return communityChallengeRepository.findUpcomingPublicChallenges(LocalDate.now());
    }

    public List<CommunityChallenge> getChallengesByCategory(Integer categoryId) {
        return communityChallengeRepository.findByCategoryId(categoryId);
    }

    public Optional<CommunityChallenge> getChallengeById(Long id) {
        return communityChallengeRepository.findById(id);
    }

    public CommunityChallenge createChallenge(CommunityChallenge challenge) {
        // Set status based on start date
        LocalDate today = LocalDate.now();
        if (challenge.getStartDate().isAfter(today)) {
            challenge.setStatus("UPCOMING");
        } else if (challenge.getStartDate().isEqual(today) || 
                  (challenge.getStartDate().isBefore(today) && challenge.getEndDate().isAfter(today))) {
            challenge.setStatus("ACTIVE");
        } else {
            challenge.setStatus("COMPLETED");
        }
        
        return communityChallengeRepository.save(challenge);
    }

    public CommunityChallenge updateChallenge(CommunityChallenge challenge) {
        return communityChallengeRepository.save(challenge);
    }

    public void deleteChallenge(Long id) {
        communityChallengeRepository.deleteById(id);
    }

    public boolean joinChallenge(User user, Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = communityChallengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            return false;
        }

        CommunityChallenge challenge = challengeOpt.get();
        
        // Check if user is already participating
        if (participationRepository.existsByCommunityChallengeAndUserAndStatus(challenge, user, "ACTIVE")) {
            return false;
        }

        // Check max participants limit
        if (challenge.getMaxParticipants() != null) {
            Integer currentParticipants = participationRepository.countActiveParticipants(challenge);
            if (currentParticipants >= challenge.getMaxParticipants()) {
                return false;
            }
        }

        // Create participation record
        ChallengeParticipation participation = new ChallengeParticipation();
        participation.setCommunityChallenge(challenge);
        participation.setUser(user);
        participation.setStatus("ACTIVE");
        
        participationRepository.save(participation);
        return true;
    }

    public void leaveChallenge(User user, Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = communityChallengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            Optional<ChallengeParticipation> participationOpt = 
                participationRepository.findByCommunityChallengeAndUser(challengeOpt.get(), user);
            if (participationOpt.isPresent()) {
                ChallengeParticipation participation = participationOpt.get();
                participation.setStatus("DROPPED_OUT");
                participationRepository.save(participation);
            }
        }
    }

    public List<ChallengeParticipation> getUserParticipations(User user) {
        return participationRepository.findByUserAndStatus(user, "ACTIVE");
    }

    public List<ChallengeParticipation> getChallengeParticipants(Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = communityChallengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            return participationRepository.findActiveParticipantsOrderedByScore(challengeOpt.get());
        }
        return List.of();
    }

    public boolean isUserParticipating(User user, Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = communityChallengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            return participationRepository.existsByCommunityChallengeAndUserAndStatus(
                challengeOpt.get(), user, "ACTIVE");
        }
        return false;
    }
}
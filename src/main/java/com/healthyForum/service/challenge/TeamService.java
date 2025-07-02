package com.healthyForum.service.challenge;

import com.healthyForum.model.User;
import com.healthyForum.model.challenge.*;
import com.healthyForum.repository.challenge.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamService {

    private final ChallengeTeamRepository teamRepository;
    private final TeamMemberRepository memberRepository;
    private final CommunityChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository participationRepository;

    public TeamService(ChallengeTeamRepository teamRepository,
                      TeamMemberRepository memberRepository,
                      CommunityChallengeRepository challengeRepository,
                      ChallengeParticipationRepository participationRepository) {
        this.teamRepository = teamRepository;
        this.memberRepository = memberRepository;
        this.challengeRepository = challengeRepository;
        this.participationRepository = participationRepository;
    }

    public ChallengeTeam createTeam(User captain, Long challengeId, String teamName, String description, Integer maxMembers) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty() || !challengeOpt.get().getIsTeamBased()) {
            throw new IllegalArgumentException("Invalid challenge or challenge is not team-based");
        }

        CommunityChallenge challenge = challengeOpt.get();

        // Create team
        ChallengeTeam team = new ChallengeTeam();
        team.setName(teamName);
        team.setDescription(description);
        team.setCommunityChallenge(challenge);
        team.setCaptain(captain);
        team.setMaxMembers(maxMembers != null ? maxMembers : 10);
        team.setCurrentMembers(1);
        team.setStatus("ACTIVE");

        ChallengeTeam savedTeam = teamRepository.save(team);

        // Add captain as team member
        TeamMember captainMember = new TeamMember();
        captainMember.setTeam(savedTeam);
        captainMember.setUser(captain);
        captainMember.setRole("CAPTAIN");
        captainMember.setStatus("ACTIVE");
        memberRepository.save(captainMember);

        // Create or update participation record
        createOrUpdateParticipation(captain, challenge, savedTeam);

        return savedTeam;
    }

    public boolean joinTeam(User user, Long teamId) {
        Optional<ChallengeTeam> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            return false;
        }

        ChallengeTeam team = teamOpt.get();

        // Check if team is full
        if (team.getCurrentMembers() >= team.getMaxMembers()) {
            return false;
        }

        // Check if user is already in a team for this challenge
        Optional<TeamMember> existingMembership = 
            memberRepository.findActiveTeamMembershipForChallenge(user, team.getCommunityChallenge().getId());
        if (existingMembership.isPresent()) {
            return false;
        }

        // Add user to team
        TeamMember newMember = new TeamMember();
        newMember.setTeam(team);
        newMember.setUser(user);
        newMember.setRole("MEMBER");
        newMember.setStatus("ACTIVE");
        memberRepository.save(newMember);

        // Update team member count
        team.setCurrentMembers(team.getCurrentMembers() + 1);
        if (team.getCurrentMembers() >= team.getMaxMembers()) {
            team.setStatus("FULL");
        }
        teamRepository.save(team);

        // Create or update participation record
        createOrUpdateParticipation(user, team.getCommunityChallenge(), team);

        return true;
    }

    public boolean leaveTeam(User user, Long teamId) {
        Optional<ChallengeTeam> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            return false;
        }

        ChallengeTeam team = teamOpt.get();
        Optional<TeamMember> memberOpt = memberRepository.findByTeamAndUser(team, user);
        
        if (memberOpt.isEmpty()) {
            return false;
        }

        TeamMember member = memberOpt.get();
        
        // Captain cannot leave their own team (must disband instead)
        if ("CAPTAIN".equals(member.getRole())) {
            return false;
        }

        // Remove member
        member.setStatus("LEFT");
        memberRepository.save(member);

        // Update team member count
        team.setCurrentMembers(team.getCurrentMembers() - 1);
        if ("FULL".equals(team.getStatus()) && team.getCurrentMembers() < team.getMaxMembers()) {
            team.setStatus("ACTIVE");
        }
        teamRepository.save(team);

        // Update participation to individual
        Optional<ChallengeParticipation> participationOpt = 
            participationRepository.findByCommunityChallengeAndUser(team.getCommunityChallenge(), user);
        if (participationOpt.isPresent()) {
            ChallengeParticipation participation = participationOpt.get();
            participation.setTeam(null);
            participationRepository.save(participation);
        }

        return true;
    }

    public boolean disbandTeam(User captain, Long teamId) {
        Optional<ChallengeTeam> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            return false;
        }

        ChallengeTeam team = teamOpt.get();
        
        // Only captain can disband
        if (!team.getCaptain().equals(captain)) {
            return false;
        }

        // Update all team members to left status
        List<TeamMember> members = memberRepository.findByTeamAndStatus(team, "ACTIVE");
        for (TeamMember member : members) {
            member.setStatus("LEFT");
            memberRepository.save(member);

            // Update participation records
            Optional<ChallengeParticipation> participationOpt = 
                participationRepository.findByCommunityChallengeAndUser(team.getCommunityChallenge(), member.getUser());
            if (participationOpt.isPresent()) {
                ChallengeParticipation participation = participationOpt.get();
                participation.setTeam(null);
                participationRepository.save(participation);
            }
        }

        // Mark team as disbanded
        team.setStatus("DISBANDED");
        team.setCurrentMembers(0);
        teamRepository.save(team);

        return true;
    }

    public List<ChallengeTeam> getTeamsForChallenge(Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            return teamRepository.findByCommunityChallenge(challengeOpt.get());
        }
        return List.of();
    }

    public List<ChallengeTeam> getAvailableTeams(Long challengeId) {
        Optional<CommunityChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            return teamRepository.findAvailableTeams(challengeOpt.get());
        }
        return List.of();
    }

    public List<TeamMember> getTeamMembers(Long teamId) {
        Optional<ChallengeTeam> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isPresent()) {
            return memberRepository.findByTeamAndStatus(teamOpt.get(), "ACTIVE");
        }
        return List.of();
    }

    public Optional<ChallengeTeam> getUserTeamForChallenge(User user, Long challengeId) {
        Optional<TeamMember> memberOpt = 
            memberRepository.findActiveTeamMembershipForChallenge(user, challengeId);
        return memberOpt.map(TeamMember::getTeam);
    }

    private void createOrUpdateParticipation(User user, CommunityChallenge challenge, ChallengeTeam team) {
        Optional<ChallengeParticipation> existingOpt = 
            participationRepository.findByCommunityChallengeAndUser(challenge, user);
        
        if (existingOpt.isPresent()) {
            ChallengeParticipation participation = existingOpt.get();
            participation.setTeam(team);
            participation.setStatus("ACTIVE");
            participationRepository.save(participation);
        } else {
            ChallengeParticipation participation = new ChallengeParticipation();
            participation.setCommunityChallenge(challenge);
            participation.setUser(user);
            participation.setTeam(team);
            participation.setStatus("ACTIVE");
            participationRepository.save(participation);
        }
    }
}
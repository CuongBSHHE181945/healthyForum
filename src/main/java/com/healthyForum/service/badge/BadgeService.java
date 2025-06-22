package com.healthyForum.service.badge;

import com.healthyForum.model.badge.Badge;
import com.healthyForum.repository.badge.BadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;

    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Optional<Badge> findById(int id){
        return badgeRepository.findById(id);
    }
}

package com.healthyForum.repository.badge;

import com.healthyForum.model.badge.BadgeSourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeSourceTypeRepository extends JpaRepository<BadgeSourceType, Integer> {}
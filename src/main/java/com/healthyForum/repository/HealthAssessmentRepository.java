package com.healthyForum.repository;

import com.healthyForum.model.HealthAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthAssessmentRepository extends JpaRepository<HealthAssessment, Long> {
    List<HealthAssessment> findByUser_IdOrderByAssessmentDateDesc(Long userId);
}

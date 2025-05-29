package com.healthyForum.repository;

import com.healthyForum.model.SleepEntry;
import com.healthyForum.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface SleepRepository extends JpaRepository<SleepEntry, Long>,
        PagingAndSortingRepository<SleepEntry, Long> {

    List<SleepEntry> findByUser(User user);
    Page<SleepEntry> findByUser(User owner, PageRequest pageRequest);
}
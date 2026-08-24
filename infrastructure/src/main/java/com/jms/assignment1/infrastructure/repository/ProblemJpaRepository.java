package com.jms.assignment1.infrastructure.repository;

import com.jms.assignment1.infrastructure.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemJpaRepository extends JpaRepository<ProblemEntity, Long> {

    List<ProblemEntity> findByChapterId(Long chapterId);
}

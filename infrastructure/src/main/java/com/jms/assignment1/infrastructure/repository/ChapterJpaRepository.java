package com.jms.assignment1.infrastructure.repository;

import com.jms.assignment1.infrastructure.entity.ChapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterJpaRepository extends JpaRepository<ChapterEntity, Long> {
}

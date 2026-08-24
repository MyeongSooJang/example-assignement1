package com.jms.assignment1.infrastructure.repository;

import com.jms.assignment1.infrastructure.entity.UserChapterSkipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserChapterSkipJpaRepository extends JpaRepository<UserChapterSkipEntity, Long> {

    Optional<UserChapterSkipEntity> findByUserIdAndChapterId(Long userId, Long chapterId);
}

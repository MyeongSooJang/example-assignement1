package com.jms.assignment1.infrastructure.repository;

import com.jms.assignment1.chapter.UserChapterSkip;
import com.jms.assignment1.infrastructure.entity.UserChapterSkipEntity;
import com.jms.assignment1.repository.UserChapterSkipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserChapterSkipRepositoryImpl implements UserChapterSkipRepository {

    private final UserChapterSkipJpaRepository userChapterSkipJpaRepository;

    @Override
    public void saveOrUpdate(UserChapterSkip skip) {
        userChapterSkipJpaRepository.save(UserChapterSkipEntity.fromDomain(skip));
    }

    @Override
    public Optional<Long> findSkippedProblemIdByUserIdAndChapterId(Long userId, Long chapterId) {
        return userChapterSkipJpaRepository.findByUserIdAndChapterId(userId, chapterId)
                                           .map(entity -> entity.getSkippedProblemId());
    }
}

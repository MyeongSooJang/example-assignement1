package com.jms.assignment1.repository;

import com.jms.assignment1.chapter.UserChapterSkip;

import java.util.Optional;

public interface UserChapterSkipRepository {

    void saveOrUpdate(UserChapterSkip skip);

    Optional<Long> findSkippedProblemIdByUserIdAndChapterId(Long userId, Long chapterId);
}

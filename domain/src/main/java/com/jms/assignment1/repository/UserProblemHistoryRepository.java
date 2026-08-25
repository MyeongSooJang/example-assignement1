package com.jms.assignment1.repository;

import com.jms.assignment1.history.UserProblemHistory;

import java.util.List;
import java.util.Optional;

public interface UserProblemHistoryRepository {

    void saveOrUpdate(UserProblemHistory history);

    Optional<UserProblemHistory> findByUserIdAndProblemId(Long userId, Long problemId);

    List<Long> findSolvedProblemIdsByUserIdAndChapterId(Long userId, Long chapterId);

    long countByProblemId(Long problemId);

    long countCorrectByProblemId(Long problemId);
}

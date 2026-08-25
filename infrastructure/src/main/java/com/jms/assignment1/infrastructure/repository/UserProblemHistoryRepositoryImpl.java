package com.jms.assignment1.infrastructure.repository;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.history.UserProblemHistory;
import com.jms.assignment1.infrastructure.entity.UserProblemHistoryEntity;
import com.jms.assignment1.repository.UserProblemHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserProblemHistoryRepositoryImpl implements UserProblemHistoryRepository {

    private final UserProblemHistoryJpaRepository userProblemHistoryJpaRepository;

    @Override
    public void saveOrUpdate(UserProblemHistory history) {
        userProblemHistoryJpaRepository.save(UserProblemHistoryEntity.fromDomain(history));
    }

    @Override
    public Optional<UserProblemHistory> findByUserIdAndProblemId(Long userId, Long problemId) {
        return userProblemHistoryJpaRepository.findByUserIdAndProblemId(userId, problemId)
                                              .map(entity -> entity.toDomain());
    }

    @Override
    public List<Long> findSolvedProblemIdsByUserIdAndChapterId(Long userId, Long chapterId) {
        return userProblemHistoryJpaRepository.findSolvedProblemIdsByUserIdAndChapterId(userId, chapterId);
    }

    @Override
    public long countByProblemId(Long problemId) {
        return userProblemHistoryJpaRepository.countByProblemId(problemId);
    }

    @Override
    public long countCorrectByProblemId(Long problemId) {
        return userProblemHistoryJpaRepository.countByProblemIdAndAnswerStatus(problemId, AnswerStatus.CORRECT);
    }
}

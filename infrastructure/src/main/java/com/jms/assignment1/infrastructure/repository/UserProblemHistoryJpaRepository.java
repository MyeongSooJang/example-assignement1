package com.jms.assignment1.infrastructure.repository;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.infrastructure.entity.UserProblemHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProblemHistoryJpaRepository extends JpaRepository<UserProblemHistoryEntity, Long> {

    Optional<UserProblemHistoryEntity> findByUserIdAndProblemId(Long userId, Long problemId);

    @Query("""
            SELECT h.problemId
            FROM UserProblemHistoryEntity h
            JOIN ProblemEntity p ON h.problemId = p.id
            WHERE h.userId = :userId
            AND p.chapterId = :chapterId
            """)
    List<Long> findSolvedProblemIdsByUserIdAndChapterId(@Param("userId") Long userId, @Param("chapterId") Long chapterId);

    long countByProblemId(@Param("problemId") Long problemId);

    @Query("""
            SELECT COUNT(h)
            FROM UserProblemHistoryEntity h
            WHERE h.problemId = :problemId
            AND h.answerStatus = :answerStatus
            """)
    long countByProblemIdAndAnswerStatus(@Param("problemId") Long problemId, @Param("answerStatus") AnswerStatus answerStatus);
}

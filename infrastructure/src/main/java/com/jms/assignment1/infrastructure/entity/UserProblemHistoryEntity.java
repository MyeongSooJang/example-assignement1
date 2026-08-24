package com.jms.assignment1.infrastructure.entity;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.history.UserProblemHistory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_problem_histories",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "problem_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProblemHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnswerStatus answerStatus;

    @Column(nullable = false)
    private String userAnswer;

    public static UserProblemHistoryEntity fromDomain(UserProblemHistory history) {
        UserProblemHistoryEntity entity = new UserProblemHistoryEntity();
        entity.id = history.getId();
        entity.userId = history.getUserId();
        entity.problemId = history.getProblemId();
        entity.answerStatus = history.getAnswerStatus();
        entity.userAnswer = history.getUserAnswer();
        return entity;
    }

    public UserProblemHistory toDomain() {
        return UserProblemHistory.reconstruct(id, userId, problemId, answerStatus, userAnswer);
    }
}

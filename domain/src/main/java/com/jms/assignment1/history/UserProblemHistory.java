package com.jms.assignment1.history;

import com.jms.assignment1.answer.Answer;
import com.jms.assignment1.answer.AnswerStatus;
import lombok.Getter;

@Getter
public class UserProblemHistory {

    private final Long id;
    private final Long userId;
    private final Long problemId;
    private final AnswerStatus answerStatus;
    private final String userAnswer;

    private UserProblemHistory(Long id, Long userId, Long problemId, AnswerStatus answerStatus, String userAnswer) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 비어있을 수 없습니다");
        }
        if (problemId == null) {
            throw new IllegalArgumentException("problemId는 비어있을 수 없습니다");
        }
        if (answerStatus == null) {
            throw new IllegalArgumentException("answerStatus는 비어있을 수 없습니다");
        }
        if (userAnswer == null || userAnswer.isBlank()) {
            throw new IllegalArgumentException("userAnswer는 비어있을 수 없습니다");
        }
        this.id = id;
        this.userId = userId;
        this.problemId = problemId;
        this.answerStatus = answerStatus;
        this.userAnswer = userAnswer;
    }

    public static UserProblemHistory create(Long userId, Long problemId, AnswerStatus answerStatus, Answer userAnswer) {
        return new UserProblemHistory(null, userId, problemId, answerStatus, userAnswer.toText());
    }

    public static UserProblemHistory reconstruct(Long id, Long userId, Long problemId, AnswerStatus answerStatus, String userAnswer) {
        return new UserProblemHistory(id, userId, problemId, answerStatus, userAnswer);
    }

    public UserProblemHistory update(AnswerStatus updateStatus, Answer userAnswer) {
        return new UserProblemHistory(this.id, this.userId, this.problemId, updateStatus, userAnswer.toText());
    }
}

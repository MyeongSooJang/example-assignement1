package com.jms.assignment1.exception;

public class ProblemHistoryNotFoundException extends DomainException {

    public ProblemHistoryNotFoundException(Long userId, Long problemId) {
        super(ErrorMessage.PROBLEM_HISTORY_NOT_FOUND.format(userId, problemId));
    }
}

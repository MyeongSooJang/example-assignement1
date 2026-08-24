package com.jms.assignment1.application.problem;

public interface GetProblemHistoryUseCase {

    ProblemHistoryResult execute(Long userId, Long problemId);
}

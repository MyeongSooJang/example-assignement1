package com.jms.assignment1.service;

import com.jms.assignment1.repository.UserProblemHistoryRepository;

public class ProblemCorrectRateService {

    private final UserProblemHistoryRepository userProblemHistoryRepository;
    private final CorrectRateCalculator correctRateCalculator;

    public ProblemCorrectRateService(UserProblemHistoryRepository userProblemHistoryRepository,
                                     CorrectRateCalculator correctRateCalculator) {
        this.userProblemHistoryRepository = userProblemHistoryRepository;
        this.correctRateCalculator = correctRateCalculator;
    }

    public Integer calculate(Long problemId) {
        long totalCount = userProblemHistoryRepository.countByProblemId(problemId);
        long correctCount = userProblemHistoryRepository.countCorrectByProblemId(problemId);
        return correctRateCalculator.calculate(totalCount, correctCount);
    }
}

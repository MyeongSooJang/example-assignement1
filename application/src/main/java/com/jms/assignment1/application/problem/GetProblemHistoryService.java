package com.jms.assignment1.application.problem;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.application.common.UserValidator;
import com.jms.assignment1.exception.ProblemHistoryNotFoundException;
import com.jms.assignment1.exception.ProblemNotFoundException;
import com.jms.assignment1.history.UserProblemHistory;
import com.jms.assignment1.problem.Problem;
import com.jms.assignment1.repository.ProblemRepository;
import com.jms.assignment1.repository.UserProblemHistoryRepository;
import com.jms.assignment1.service.CorrectRateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProblemHistoryService implements GetProblemHistoryUseCase {

    private final UserValidator userValidator;
    private final ProblemRepository problemRepository;
    private final UserProblemHistoryRepository userProblemHistoryRepository;

    @Override
    public ProblemHistoryResult execute(Long userId, Long problemId) {
        userValidator.validate(userId);

        Problem problem = problemRepository.findById(problemId)
                                           .orElseThrow(() -> new ProblemNotFoundException(problemId));

        UserProblemHistory userProblemHistory = userProblemHistoryRepository.findByUserIdAndProblemId(userId, problemId)
                                                                             .orElseThrow(() -> new ProblemHistoryNotFoundException(userId, problemId));

        Integer answerCorrectRate = calculateAnswerCorrectRate(problemId);

        return new ProblemHistoryResult(problem, userProblemHistory, answerCorrectRate);
    }

    private Integer calculateAnswerCorrectRate(Long problemId) {
        List<AnswerStatus> answerStatuses = userProblemHistoryRepository.findAnswerStatusesByProblemId(problemId);
        long correctCount = answerStatuses.stream()
                                          .filter(status -> status == AnswerStatus.CORRECT)
                                          .count();
        return CorrectRateCalculator.calculate(answerStatuses.size(), correctCount);
    }
}

package com.jms.assignment1.application.problem;

import com.jms.assignment1.history.UserProblemHistory;
import com.jms.assignment1.problem.Problem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProblemHistoryResult {

    private final Problem problem;
    private final UserProblemHistory userProblemHistory;
    private final Integer answerCorrectRate;
}

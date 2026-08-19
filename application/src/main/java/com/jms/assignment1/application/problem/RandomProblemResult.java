package com.jms.assignment1.application.problem;

import com.jms.assignment1.problem.Problem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RandomProblemResult {

    private final Problem problem;
    private final Integer answerCorrectRate;
}

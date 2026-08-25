package com.jms.assignment1.api.problem.dto;

import com.jms.assignment1.application.problem.RandomProblemResult;
import com.jms.assignment1.problem.MultipleChoiceProblem;
import com.jms.assignment1.problem.Problem;

import java.util.List;

public record GetRandomProblemResponse(
        Long problemId,
        String content,
        List<String> choices,
        Integer answerCorrectRate
) {

    public static GetRandomProblemResponse from(RandomProblemResult randomProblemResult) {
        Problem problem = randomProblemResult.getProblem();
        List<String> choices = null;
        if (problem instanceof MultipleChoiceProblem multipleChoiceProblem) {
            choices = multipleChoiceProblem.getChoices();
        }
        return new GetRandomProblemResponse(
                problem.getId(), problem.getContent(), choices, randomProblemResult.getAnswerCorrectRate());
    }
}

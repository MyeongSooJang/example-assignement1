package com.jms.assignment1.api.problem.dto;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.application.problem.ProblemHistoryResult;
import com.jms.assignment1.history.UserProblemHistory;
import com.jms.assignment1.problem.MultipleChoiceProblem;
import com.jms.assignment1.problem.Problem;
import com.jms.assignment1.problem.ShortAnswerProblem;

import java.util.Arrays;
import java.util.List;

public record GetProblemHistoryResponse(
        Long problemId,
        AnswerStatus answerStatus,
        String explanation,
        Object problemAnswers,
        Object userAnswers,
        Integer answerCorrectRate
) {

    public static GetProblemHistoryResponse from(ProblemHistoryResult problemHistoryResult) {
        Problem problem = problemHistoryResult.getProblem();
        UserProblemHistory userProblemHistory = problemHistoryResult.getUserProblemHistory();

        Object problemAnswers;
        Object userAnswers;
        if (problem instanceof MultipleChoiceProblem multipleChoiceProblem) {
            problemAnswers = multipleChoiceProblem.getCorrectAnswers();
            userAnswers = parseSelectedChoices(userProblemHistory.getUserAnswer());
        } else if (problem instanceof ShortAnswerProblem shortAnswerProblem) {
            problemAnswers = shortAnswerProblem.getCorrectAnswer();
            userAnswers = userProblemHistory.getUserAnswer();
        } else {
            throw new IllegalStateException("알 수 없는 문제 타입입니다: " + problem.getClass());
        }

        return new GetProblemHistoryResponse(
                problem.getId(),
                userProblemHistory.getAnswerStatus(),
                problem.getSolution(),
                problemAnswers,
                userAnswers,
                problemHistoryResult.getAnswerCorrectRate());
    }

    private static List<Integer> parseSelectedChoices(String userAnswer) {
        return Arrays.stream(userAnswer.split(","))
                     .map(Integer::parseInt)
                     .toList();
    }
}

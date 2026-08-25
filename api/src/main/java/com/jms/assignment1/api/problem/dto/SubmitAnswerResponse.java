package com.jms.assignment1.api.problem.dto;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.application.problem.SubmitAnswerResult;

public record SubmitAnswerResponse(
        AnswerStatus answerStatus,
        String explanation
) {

    public static SubmitAnswerResponse from(SubmitAnswerResult submitAnswerResult) {
        return new SubmitAnswerResponse(submitAnswerResult.getAnswerStatus(), submitAnswerResult.getExplanation());
    }
}

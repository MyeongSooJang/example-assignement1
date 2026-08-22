package com.jms.assignment1.application.problem;

import com.jms.assignment1.answer.AnswerStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SubmitAnswerResult {

    private final AnswerStatus answerStatus;
    private final String explanation;
}

package com.jms.assignment1.application.problem;

import com.jms.assignment1.answer.Answer;
import com.jms.assignment1.answer.AnswerStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class AnswerEvaluation {

    private final AnswerStatus answerStatus;
    private final Answer answer;
}

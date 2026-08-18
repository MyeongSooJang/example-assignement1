package com.jms.assignment1.problem;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.answer.ShortAnswer;

public class ShortAnswerProblem extends Problem {

    private final String correctAnswer;

    public ShortAnswerProblem(Long id,
                              Long chapterId,
                              String content,
                              String solution,
                              String correctAnswer) {
        super(id, chapterId, content, solution);
        if (correctAnswer == null || correctAnswer.isBlank()) {
            throw new IllegalArgumentException("correctAnswer는 blank일 수 없습니다");
        }
        this.correctAnswer = correctAnswer;
    }

    public AnswerStatus evaluate(ShortAnswer shortAnswer) {
        if (shortAnswer.getText().strip().equals(correctAnswer.strip())) {
            return AnswerStatus.CORRECT;
        }
        return AnswerStatus.WRONG;
    }
}

package com.jms.assignment1.problem;

import com.jms.assignment1.answer.Answer;
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

    @Override
    public AnswerStatus evaluate(Answer answer) {
        if (!(answer instanceof ShortAnswer sa)) {
            throw new IllegalArgumentException("주관식 답안이 필요합니다");
        }
        if (sa.getText().strip().equals(correctAnswer.strip())) {
            return AnswerStatus.CORRECT;
        }
        return AnswerStatus.WRONG;
    }
}

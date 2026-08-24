package com.jms.assignment1.problem;

import com.jms.assignment1.answer.Answer;
import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.answer.ShortAnswer;
import lombok.Getter;

import java.util.List;

public class ShortAnswerProblem extends Problem {

    @Getter
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
    public Answer createAnswer(List<Integer> selectedChoices, String text) {
        return new ShortAnswer(text);
    }

    @Override
    public AnswerStatus evaluate(Answer answer) {
        return evaluate((ShortAnswer) answer);
    }

    public AnswerStatus evaluate(ShortAnswer shortAnswer) {
        if (shortAnswer.getText().strip().equals(correctAnswer.strip())) {
            return AnswerStatus.CORRECT;
        }
        return AnswerStatus.WRONG;
    }
}

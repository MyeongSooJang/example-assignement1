package com.jms.assignment1.problem;

public class ShortAnswerProblem extends Problem {

    private final String correctAnswer;

    public ShortAnswerProblem(Long id,
                              Long chapterId,
                              String content,
                              String solution,
                              String correctAnswer)
    {
        super(id, chapterId, content, solution);
        if (correctAnswer == null || correctAnswer.isBlank()){
            throw new IllegalArgumentException("correctAnswer는 blank일 수 없습니다");
        }
        this.correctAnswer = correctAnswer;
    }
}

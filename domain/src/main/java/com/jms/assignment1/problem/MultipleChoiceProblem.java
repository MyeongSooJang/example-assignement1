package com.jms.assignment1.problem;

import java.util.List;

public class MultipleChoiceProblem extends Problem {

    private final List<String> choices;
    private final List<Integer> correctAnswers;

    public MultipleChoiceProblem(Long id,
                                 Long chapterId,
                                 String content,
                                 String solution,
                                 List<String> choices,
                                 List<Integer> correctAnswers) {
        super(id, chapterId, content, solution);
        if (choices == null || choices.isEmpty()) {
            throw new IllegalArgumentException("choices는 비어있을 수 없습니다");
        }
        if (correctAnswers == null || correctAnswers.isEmpty()) {
            throw new IllegalArgumentException("correctAnswers는 비어있을 수 없습니다");
        }
        this.choices = choices;
        this.correctAnswers = correctAnswers;
    }
}

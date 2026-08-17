package com.jms.assignment1.problem;

import java.util.List;

public class MultipleChoiceProblem extends Problem {
    private final List<Integer> choices;
    private final List<Integer> correctAnswers;

    public MultipleChoiceProblem(List<Integer> choices, List<Integer> correctAnswers) {
        this.choices = choices;
        this.correctAnswers = correctAnswers;
    }
}

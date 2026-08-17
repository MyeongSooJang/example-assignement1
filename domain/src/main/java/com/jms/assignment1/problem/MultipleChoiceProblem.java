package com.jms.assignment1.problem;

import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MultipleChoiceProblem extends Problem {
    private List<String> choices;
    private List<Integer> correctAnswer;
}

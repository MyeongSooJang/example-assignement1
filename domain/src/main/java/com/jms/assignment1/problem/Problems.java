package com.jms.assignment1.problem;

import com.jms.assignment1.exception.NoAvailableProblemException;

import java.util.List;
import java.util.Set;

public class Problems {

    private final Long chapterId;
    private final List<Problem> problems;

    public Problems(Long chapterId, List<Problem> problems) {
        this.chapterId = chapterId;
        this.problems = List.copyOf(problems);
    }

    public Problems excluding(Set<Long> excludedProblemIds) {
        List<Problem> availableProblems = problems.stream()
                .filter(problem -> !excludedProblemIds.contains(problem.getId()))
                .toList();
        return new Problems(chapterId, availableProblems);
    }

    public Problem pickRandom() {
        if (problems.isEmpty()) {
            throw new NoAvailableProblemException(chapterId);
        }
        return problems.get((int) (Math.random() * problems.size()));
    }
}

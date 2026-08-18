package com.jms.assignment1.problem;

import com.jms.assignment1.answer.Answer;
import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.answer.MultipleChoiceAnswer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public AnswerStatus evaluate(Answer answer) {
        if (!(answer instanceof MultipleChoiceAnswer mca)) {
            throw new IllegalArgumentException("객관식 답안이 필요합니다");
        }
        Set<Integer> userAnswers = new HashSet<>(mca.getSelectedChoices());
        Set<Integer> correctAnswerSet = new HashSet<>(correctAnswers);
        if (userAnswers.equals(correctAnswerSet)) {
            return AnswerStatus.CORRECT;
        }
        long matchCount = userAnswers.stream()
                                     .filter(correctAnswerSet::contains)
                                     .count();
        if (matchCount == 0) {
            return AnswerStatus.WRONG;
        }
        return AnswerStatus.PARTIAL;
    }
}

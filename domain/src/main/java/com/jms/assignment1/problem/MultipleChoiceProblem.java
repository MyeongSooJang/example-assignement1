package com.jms.assignment1.problem;

import com.jms.assignment1.answer.Answer;
import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.answer.MultipleChoiceAnswer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;

public class MultipleChoiceProblem extends Problem {

    @Getter
    private final List<String> choices;
    private final Set<Integer> correctAnswerSet;

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
        this.correctAnswerSet = new HashSet<>(correctAnswers);
    }

    @Override
    public Answer createAnswer(List<Integer> selectedChoices, String text) {
        return new MultipleChoiceAnswer(selectedChoices);
    }

    @Override
    public AnswerStatus evaluate(Answer answer) {
        return evaluate((MultipleChoiceAnswer) answer);
    }

    public AnswerStatus evaluate(MultipleChoiceAnswer multipleChoiceAnswer) {
        Set<Integer> selectedChoices = new HashSet<>(multipleChoiceAnswer.getSelectedChoices());
        if (selectedChoices.equals(correctAnswerSet)) {
            return AnswerStatus.CORRECT;
        }
        long matchCount = selectedChoices.stream()
                                         .filter(correctAnswerSet::contains)
                                         .count();
        if (matchCount == 0) {
            return AnswerStatus.WRONG;
        }
        return AnswerStatus.PARTIAL;
    }

    public boolean isMultipleAnswer() {
        return correctAnswerSet.size() > 1;
    }

    public List<Integer> getCorrectAnswers() {
        return new ArrayList<>(correctAnswerSet);
    }
}

package com.jms.assignment1.answer;

import java.util.List;

public class MultipleChoiceAnswer implements Answer {

    private final List<Integer> selectedChoices;

    public MultipleChoiceAnswer(List<Integer> selectedChoices) {
        if (selectedChoices == null || selectedChoices.isEmpty()) {
            throw new IllegalArgumentException("selectedChoices는 비어있을 수 없습니다");
        }
        this.selectedChoices = selectedChoices;
    }
}

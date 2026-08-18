package com.jms.assignment1.answer;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class MultipleChoiceAnswer implements Answer {

    private final List<Integer> selectedChoices;

    public MultipleChoiceAnswer(List<Integer> selectedChoices) {
        if (selectedChoices == null || selectedChoices.isEmpty()) {
            throw new IllegalArgumentException("selectedChoices는 비어있을 수 없습니다");
        }
        this.selectedChoices = selectedChoices;
    }

    @Override
    public String toText() {
        return selectedChoices.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}

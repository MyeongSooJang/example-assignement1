package com.jms.assignment1.answer;

import java.util.List;

public class MultipleChoiceAnswer implements Answer{
    private final List<String> choices;

    public MultipleChoiceAnswer(List<String> choices) {
        this.choices = choices;
    }
}

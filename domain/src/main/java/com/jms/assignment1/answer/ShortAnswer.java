package com.jms.assignment1.answer;

import lombok.Getter;

@Getter
public class ShortAnswer implements Answer {

    private final String text;

    public ShortAnswer(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text는 비어있을 수 없습니다");
        }
        this.text = text;
    }

    @Override
    public String toText() {
        return text;
    }
}

package com.jms.assignment1.answer;

public class ShortAnswer implements Answer {

    private final String text;

    public ShortAnswer(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text는 비어있을 수 없습니다");
        }
        this.text = text;
    }
}

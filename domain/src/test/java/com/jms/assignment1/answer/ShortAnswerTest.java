package com.jms.assignment1.answer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShortAnswerTest {

    @Test
    void text가_null이면_예외() {
        assertThatThrownBy(() -> new ShortAnswer(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void text가_blank이면_예외() {
        assertThatThrownBy(() -> new ShortAnswer("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

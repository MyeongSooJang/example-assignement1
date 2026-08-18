package com.jms.assignment1.answer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MultipleChoiceAnswerTest {

    @Test
    void selectedChoices가_null이면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceAnswer(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void selectedChoices가_비어있으면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceAnswer(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

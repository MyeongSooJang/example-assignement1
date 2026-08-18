package com.jms.assignment1.answer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void toText_단일_선택지는_숫자_그대로_반환() {
        assertThat(new MultipleChoiceAnswer(List.of(1)).toText())
                .isEqualTo("1");
    }

    @Test
    void toText_복수_선택지는_쉼표로_구분하여_반환() {
        assertThat(new MultipleChoiceAnswer(List.of(1, 2, 3)).toText())
                .isEqualTo("1,2,3");
    }
}

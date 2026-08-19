package com.jms.assignment1.history;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.answer.MultipleChoiceAnswer;
import com.jms.assignment1.answer.ShortAnswer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserProblemHistoryTest {

    @Test
    void userId가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> UserProblemHistory.create(null, 1L, AnswerStatus.CORRECT, new ShortAnswer("서울")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void problemId가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> UserProblemHistory.create(1L, null, AnswerStatus.CORRECT, new ShortAnswer("서울")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void answerStatus가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> UserProblemHistory.create(1L, 1L, null, new ShortAnswer("서울")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주관식_답안은_문자열로_변환되어_저장된다() {
        UserProblemHistory history = UserProblemHistory.create(1L, 1L, AnswerStatus.CORRECT, new ShortAnswer("서울"));

        assertThat(history.getUserAnswer()).isEqualTo("서울");
    }

    @Test
    void 객관식_답안은_문자열로_변환되어_저장된다() {
        UserProblemHistory history = UserProblemHistory.create(1L, 1L, AnswerStatus.PARTIAL, new MultipleChoiceAnswer(List.of(1, 3)));

        assertThat(history.getUserAnswer()).isEqualTo("1,3");
    }

    @Test
    void 업데이트_시_answerStatus와_userAnswer가_변경된다() {
        UserProblemHistory history = UserProblemHistory.create(1L, 1L, AnswerStatus.WRONG, new ShortAnswer("부산"));
        UserProblemHistory updated = history.update(AnswerStatus.CORRECT, new ShortAnswer("서울"));

        assertThat(updated.getAnswerStatus()).isEqualTo(AnswerStatus.CORRECT);
        assertThat(updated.getUserAnswer()).isEqualTo("서울");
    }

    @Test
    void 업데이트_시_id와_userId와_problemId는_유지된다() {
        UserProblemHistory history = UserProblemHistory.create(1L, 1L, AnswerStatus.WRONG, new ShortAnswer("부산"));
        UserProblemHistory updated = history.update(AnswerStatus.CORRECT, new ShortAnswer("서울"));

        assertThat(updated.getUserId()).isEqualTo(history.getUserId());
        assertThat(updated.getProblemId()).isEqualTo(history.getProblemId());
    }
}

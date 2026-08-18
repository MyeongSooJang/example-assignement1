package com.jms.assignment1.problem;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.answer.MultipleChoiceAnswer;
import com.jms.assignment1.answer.ShortAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MultipleChoiceProblemTest {

    private MultipleChoiceProblem problem;

    @BeforeEach
    void setUp() {
        problem = new MultipleChoiceProblem(
                1L, 1L, "다음 중 옳은 것을 모두 고르시오.", "해설",
                List.of("가", "나", "다", "라", "마"), List.of(1, 2));
    }

    // --- 생성자 검증 테스트 ---

    @Test
    void id가_null이면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceProblem(null, 1L, "문제", "해설", List.of("가"), List.of(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void chapterId가_null이면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceProblem(1L, null, "문제", "해설", List.of("가"), List.of(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_null이면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceProblem(1L, 1L, null, "해설", List.of("가"), List.of(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_blank이면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceProblem(1L, 1L, "   ", "해설", List.of("가"), List.of(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void solution이_null이면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceProblem(1L, 1L, "문제", null, List.of("가"), List.of(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void solution이_blank이면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceProblem(1L, 1L, "문제", "   ", List.of("가"), List.of(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void choices가_null이면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceProblem(1L, 1L, "문제", "해설", null, List.of(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void choices가_비어있으면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceProblem(1L, 1L, "문제", "해설", List.of(), List.of(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void correctAnswers가_null이면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceProblem(1L, 1L, "문제", "해설", List.of("가"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void correctAnswers가_비어있으면_예외() {
        assertThatThrownBy(() -> new MultipleChoiceProblem(1L, 1L, "문제", "해설", List.of("가"), List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- evaluate 테스트 ---

    @Test
    void 정답을_모두_선택하면_CORRECT() {
        assertThat(problem.evaluate(new MultipleChoiceAnswer(List.of(1, 2))))
                .isEqualTo(AnswerStatus.CORRECT);
    }

    @Test
    void 정답만_부분적으로_선택하면_PARTIAL() {
        assertThat(problem.evaluate(new MultipleChoiceAnswer(List.of(1))))
                .isEqualTo(AnswerStatus.PARTIAL);
    }

    @Test
    void 정답과_오답을_함께_선택하면_PARTIAL() {
        assertThat(problem.evaluate(new MultipleChoiceAnswer(List.of(1, 3))))
                .isEqualTo(AnswerStatus.PARTIAL);
    }

    @Test
    void 오답만_선택하면_WRONG() {
        assertThat(problem.evaluate(new MultipleChoiceAnswer(List.of(3, 4))))
                .isEqualTo(AnswerStatus.WRONG);
    }

    @Test
    void 중복_선택해도_정답_판정에_영향없음() {
        assertThat(problem.evaluate(new MultipleChoiceAnswer(List.of(1, 1))))
                .isEqualTo(AnswerStatus.PARTIAL);
    }

    @Test
    void 주관식_답안을_넘기면_예외() {
        assertThatThrownBy(() -> problem.evaluate(new ShortAnswer("답")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

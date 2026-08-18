package com.jms.assignment1.problem;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.answer.MultipleChoiceAnswer;
import com.jms.assignment1.answer.ShortAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShortAnswerProblemTest {

    private ShortAnswerProblem problem;

    @BeforeEach
    void setUp() {
        problem = new ShortAnswerProblem(1L, 1L, "대한민국의 수도는?", "서울은 조선시대부터 수도였습니다.", "서울");
    }

    // --- 생성자 검증 테스트 ---

    @Test
    void id가_null이면_예외() {
        assertThatThrownBy(() -> new ShortAnswerProblem(null, 1L, "문제", "해설", "정답"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void chapterId가_null이면_예외() {
        assertThatThrownBy(() -> new ShortAnswerProblem(1L, null, "문제", "해설", "정답"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_null이면_예외() {
        assertThatThrownBy(() -> new ShortAnswerProblem(1L, 1L, null, "해설", "정답"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_blank이면_예외() {
        assertThatThrownBy(() -> new ShortAnswerProblem(1L, 1L, "   ", "해설", "정답"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void solution이_null이면_예외() {
        assertThatThrownBy(() -> new ShortAnswerProblem(1L, 1L, "문제", null, "정답"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void solution이_blank이면_예외() {
        assertThatThrownBy(() -> new ShortAnswerProblem(1L, 1L, "문제", "   ", "정답"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void correctAnswer가_null이면_예외() {
        assertThatThrownBy(() -> new ShortAnswerProblem(1L, 1L, "문제", "해설", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void correctAnswer가_blank이면_예외() {
        assertThatThrownBy(() -> new ShortAnswerProblem(1L, 1L, "문제", "해설", "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- evaluate 테스트 ---

    @Test
    void 정확히_일치하면_CORRECT() {
        assertThat(problem.evaluate(new ShortAnswer("서울")))
                .isEqualTo(AnswerStatus.CORRECT);
    }

    @Test
    void 앞뒤_공백이_있어도_CORRECT() {
        assertThat(problem.evaluate(new ShortAnswer("  서울  ")))
                .isEqualTo(AnswerStatus.CORRECT);
    }

    @Test
    void 다른_답이면_WRONG() {
        assertThat(problem.evaluate(new ShortAnswer("부산")))
                .isEqualTo(AnswerStatus.WRONG);
    }

    @Test
    void 일부만_일치해도_WRONG() {
        assertThat(problem.evaluate(new ShortAnswer("서울시")))
                .isEqualTo(AnswerStatus.WRONG);
    }

    @Test
    void 중간_공백이_있으면_WRONG() {
        assertThat(problem.evaluate(new ShortAnswer("서 울")))
                .isEqualTo(AnswerStatus.WRONG);
    }

    @Test
    void 정답에_공백이_포함된_경우_정확히_일치하면_CORRECT() {
        ShortAnswerProblem multiWordProblem = new ShortAnswerProblem(
                2L, 1L, "독도는 어느 나라 땅?", "해설", "대한민국 영토");
        assertThat(multiWordProblem.evaluate(new ShortAnswer("대한민국 영토")))
                .isEqualTo(AnswerStatus.CORRECT);
    }

    @Test
    void 정답에_공백이_포함된_경우_공백을_제거하면_WRONG() {
        ShortAnswerProblem multiWordProblem = new ShortAnswerProblem(
                2L, 1L, "독도는 어느 나라 땅?", "해설", "대한민국 영토");
        assertThat(multiWordProblem.evaluate(new ShortAnswer("대한민국영토")))
                .isEqualTo(AnswerStatus.WRONG);
    }

    @Test
    void 객관식_답안을_넘기면_예외() {
        assertThatThrownBy(() -> problem.evaluate(new MultipleChoiceAnswer(List.of(1))))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

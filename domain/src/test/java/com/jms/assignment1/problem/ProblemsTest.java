package com.jms.assignment1.problem;

import com.jms.assignment1.exception.NoAvailableProblemException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProblemsTest {

    private static final Long CHAPTER_ID = 1L;

    private MultipleChoiceProblem 문제_생성(Long id) {
        return new MultipleChoiceProblem(id, CHAPTER_ID, "문제 내용", "해설", List.of("1번", "2번", "3번", "4번", "5번"), List.of(1));
    }

    @Test
    void 가용_문제가_없으면_pickRandom_시_예외가_발생한다() {
        Problems emptyProblems = new Problems(CHAPTER_ID, List.of());

        assertThatThrownBy(emptyProblems::pickRandom)
                .isInstanceOf(NoAvailableProblemException.class);
    }

    @Test
    void excluding_으로_제외된_문제는_반환되지_않는다() {
        Problem 문제1 = 문제_생성(1L);
        Problem 문제2 = 문제_생성(2L);
        Problem 문제3 = 문제_생성(3L);
        Problems problems = new Problems(CHAPTER_ID, List.of(문제1, 문제2, 문제3));

        Problems availableProblems = problems.excluding(Set.of(1L, 2L));

        Problem selected = availableProblems.pickRandom();
        assertThat(selected.getId()).isEqualTo(3L);
    }

    @Test
    void excluding_으로_모든_문제가_제외되면_pickRandom_시_예외가_발생한다() {
        Problem 문제1 = 문제_생성(1L);
        Problems problems = new Problems(CHAPTER_ID, List.of(문제1));

        Problems availableProblems = problems.excluding(Set.of(1L));

        assertThatThrownBy(availableProblems::pickRandom)
                .isInstanceOf(NoAvailableProblemException.class);
    }

    @Test
    void pickRandom_은_목록_안의_문제를_반환한다() {
        Problem 문제1 = 문제_생성(1L);
        Problem 문제2 = 문제_생성(2L);
        Problems problems = new Problems(CHAPTER_ID, List.of(문제1, 문제2));

        Problem selected = problems.pickRandom();

        assertThat(selected.getId()).isIn(1L, 2L);
    }
}

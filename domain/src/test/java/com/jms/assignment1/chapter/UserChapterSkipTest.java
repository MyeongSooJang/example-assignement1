package com.jms.assignment1.chapter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserChapterSkipTest {

    @Test
    void userId가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> UserChapterSkip.create(null, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void chapterId가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> UserChapterSkip.create(1L, null, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void skippedProblemId가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> UserChapterSkip.create(1L, 1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 업데이트_시_skippedProblemId가_변경된다() {
        UserChapterSkip skip = UserChapterSkip.create(1L, 1L, 10L);
        UserChapterSkip updated = skip.update(20L);

        assertThat(updated.getSkippedProblemId()).isEqualTo(20L);
    }

    @Test
    void 업데이트_시_id와_userId와_chapterId는_유지된다() {
        UserChapterSkip skip = UserChapterSkip.create(1L, 1L, 10L);
        UserChapterSkip updated = skip.update(20L);

        assertThat(updated.getUserId()).isEqualTo(skip.getUserId());
        assertThat(updated.getChapterId()).isEqualTo(skip.getChapterId());
    }
}

package com.jms.assignment1.chapter;

import lombok.Getter;

@Getter
public class UserChapterSkip {

    private final Long userId;
    private final Long chapterId;
    private final Long skippedProblemId;

    public UserChapterSkip(Long userId, Long chapterId, Long skippedProblemId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 비어있을 수 없습니다");
        }
        if (chapterId == null) {
            throw new IllegalArgumentException("chapterId는 비어있을 수 없습니다");
        }
        if (skippedProblemId == null) {
            throw new IllegalArgumentException("skippedProblemId는 비어있을 수 없습니다");
        }
        this.userId = userId;
        this.chapterId = chapterId;
        this.skippedProblemId = skippedProblemId;
    }
}

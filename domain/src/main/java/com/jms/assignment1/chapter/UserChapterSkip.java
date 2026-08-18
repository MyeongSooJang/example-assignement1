package com.jms.assignment1.chapter;

import lombok.Getter;

@Getter
public class UserChapterSkip {

    private final Long id;
    private final Long userId;
    private final Long chapterId;
    private final Long skippedProblemId;

    private UserChapterSkip(Long id, Long userId, Long chapterId, Long skippedProblemId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 비어있을 수 없습니다");
        }
        if (chapterId == null) {
            throw new IllegalArgumentException("chapterId는 비어있을 수 없습니다");
        }
        if (skippedProblemId == null) {
            throw new IllegalArgumentException("skippedProblemId는 비어있을 수 없습니다");
        }
        this.id = id;
        this.userId = userId;
        this.chapterId = chapterId;
        this.skippedProblemId = skippedProblemId;
    }

    public static UserChapterSkip create(Long userId, Long chapterId, Long skippedProblemId) {
        return new UserChapterSkip(null, userId, chapterId, skippedProblemId);
    }

    public UserChapterSkip update(Long skippedProblemId) {
        return new UserChapterSkip(this.id, this.userId, this.chapterId, skippedProblemId);
    }
}

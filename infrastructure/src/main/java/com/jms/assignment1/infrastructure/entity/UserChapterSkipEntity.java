package com.jms.assignment1.infrastructure.entity;

import com.jms.assignment1.chapter.UserChapterSkip;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_chapter_skips",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "chapter_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChapterSkipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    @Column(nullable = false)
    private Long skippedProblemId;

    public static UserChapterSkipEntity fromDomain(UserChapterSkip skip) {
        UserChapterSkipEntity entity = new UserChapterSkipEntity();
        entity.id = skip.getId();
        entity.userId = skip.getUserId();
        entity.chapterId = skip.getChapterId();
        entity.skippedProblemId = skip.getSkippedProblemId();
        return entity;
    }

    public UserChapterSkip toDomain() {
        return UserChapterSkip.reconstruct(id, userId, chapterId, skippedProblemId);
    }
}

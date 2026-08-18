package com.jms.assignment1.problem;

import com.jms.assignment1.answer.Answer;
import com.jms.assignment1.answer.AnswerStatus;
import lombok.Getter;

@Getter
public abstract class Problem {

    private final Long id;
    private final Long chapterId;
    private final String content;
    private final String solution;

    protected Problem(Long id, Long chapterId, String content, String solution) {
        if (id == null) {
            throw new IllegalArgumentException("id는 비어있을 수 없습니다");
        }
        if (chapterId == null) {
            throw new IllegalArgumentException("chapterId는 비어있을 수 없습니다");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content는 비어있을 수 없습니다");
        }
        if (solution == null || solution.isBlank()) {
            throw new IllegalArgumentException("solution은 비어있을 수 없습니다");
        }
        this.id = id;
        this.chapterId = chapterId;
        this.content = content;
        this.solution = solution;
    }

    public abstract AnswerStatus evaluate(Answer answer);
}

package com.jms.assignment1.exception;

public enum ErrorMessage {

    USER_NOT_FOUND("존재하지 않는 사용자입니다. userId: %d"),
    CHAPTER_NOT_FOUND("존재하지 않는 단원입니다. chapterId: %d"),
    PROBLEM_NOT_FOUND("존재하지 않는 문제입니다. problemId: %d"),
    NO_AVAILABLE_PROBLEM("풀 수 있는 문제가 없습니다. chapterId: %d"),
    PROBLEM_HISTORY_NOT_FOUND("풀이 기록이 존재하지 않습니다. userId: %d, problemId: %d");

    private final String template;

    ErrorMessage(String template) {
        this.template = template;
    }

    public String format(Long id) {
        return template.formatted(id);
    }

    public String format(Long firstId, Long secondId) {
        return template.formatted(firstId, secondId);
    }
}

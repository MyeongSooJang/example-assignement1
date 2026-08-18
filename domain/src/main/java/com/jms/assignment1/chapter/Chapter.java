package com.jms.assignment1.chapter;

import lombok.Getter;

@Getter
public class Chapter {

    private final Long id;
    private final String name;

    public Chapter(Long id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("id는 비어있을 수 없습니다");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다");
        }
        this.id = id;
        this.name = name;
    }
}

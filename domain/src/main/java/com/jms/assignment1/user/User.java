package com.jms.assignment1.user;

import lombok.Getter;

@Getter
public class User {

    private final Long id;
    private final String name;

    public User(Long id, String name) {
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

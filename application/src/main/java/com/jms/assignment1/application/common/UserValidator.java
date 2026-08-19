package com.jms.assignment1.application.common;

import com.jms.assignment1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validate(Long userId) {
        userRepository.findById(userId)
                      .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다. userId: " + userId));
    }
}

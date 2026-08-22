package com.jms.assignment1.application.common;

import com.jms.assignment1.exception.UserNotFoundException;
import com.jms.assignment1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validate(Long userId) {
        userRepository.findById(userId)
                      .orElseThrow(() -> new UserNotFoundException(userId));
    }
}

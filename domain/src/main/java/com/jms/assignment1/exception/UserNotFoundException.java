package com.jms.assignment1.exception;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(Long userId) {
        super(ErrorMessage.USER_NOT_FOUND.format(userId));
    }
}

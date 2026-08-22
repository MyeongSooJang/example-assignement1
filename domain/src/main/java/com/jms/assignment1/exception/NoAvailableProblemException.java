package com.jms.assignment1.exception;

public class NoAvailableProblemException extends DomainException {

    public NoAvailableProblemException(Long chapterId) {
        super(ErrorMessage.NO_AVAILABLE_PROBLEM.format(chapterId));
    }
}

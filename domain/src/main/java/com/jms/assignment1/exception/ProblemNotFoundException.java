package com.jms.assignment1.exception;

public class ProblemNotFoundException extends DomainException {

    public ProblemNotFoundException(Long problemId) {
        super(ErrorMessage.PROBLEM_NOT_FOUND.format(problemId));
    }
}

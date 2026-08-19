package com.jms.assignment1.application.problem;

public interface GetRandomProblemUseCase {

    RandomProblemResult execute(Long userId, Long chapterId);
}

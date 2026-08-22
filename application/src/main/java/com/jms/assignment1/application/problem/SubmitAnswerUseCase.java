package com.jms.assignment1.application.problem;

import java.util.List;

public interface SubmitAnswerUseCase {

    SubmitAnswerResult execute(Long userId, Long problemId, List<Integer> selectedChoices, String text);
}

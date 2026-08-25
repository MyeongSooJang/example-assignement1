package com.jms.assignment1.api.problem.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmitAnswerRequest(
        @NotNull Long userId,
        @NotNull AnswerType answerType,
        List<@Min(1) @Max(5) Integer> selectedChoices,
        String text
) {

    @AssertTrue(message = "answerType과 제출한 답안이 일치하지 않습니다.")
    public boolean isAnswerMatchingType() {
        if (answerType == AnswerType.MULTIPLE_CHOICE) {
            return selectedChoices != null && !selectedChoices.isEmpty() && text == null;
        }
        if (answerType == AnswerType.SHORT_ANSWER) {
            return text != null && !text.isBlank() && selectedChoices == null;
        }
        return false;
    }
}

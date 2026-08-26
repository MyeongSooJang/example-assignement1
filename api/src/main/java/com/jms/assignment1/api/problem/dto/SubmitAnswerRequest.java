package com.jms.assignment1.api.problem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "문제 제출 요청")
public record SubmitAnswerRequest(
        @Schema(description = "사용자 ID", example = "1") @NotNull Long userId,
        @Schema(description = "답안 타입 (MULTIPLE_CHOICE: 객관식, SHORT_ANSWER: 주관식)", example = "MULTIPLE_CHOICE")
        @NotNull AnswerType answerType,
        @Schema(description = "객관식 선택지 번호 목록 (answerType이 MULTIPLE_CHOICE일 때 필수)", example = "[1, 3, 5]")
        List<@Min(1) @Max(5) Integer> selectedChoices,
        @Schema(description = "주관식 답안 텍스트 (answerType이 SHORT_ANSWER일 때 필수)", example = "스택")
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

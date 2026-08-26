package com.jms.assignment1.api.problem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record GetRandomProblemRequest(
        @Schema(description = "단원 ID", example = "1") @NotNull Long chapterId,
        @Schema(description = "사용자 ID", example = "1") @NotNull Long userId
) {
}

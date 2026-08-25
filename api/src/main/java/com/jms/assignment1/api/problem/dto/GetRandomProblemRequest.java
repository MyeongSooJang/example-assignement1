package com.jms.assignment1.api.problem.dto;

import jakarta.validation.constraints.NotNull;

public record GetRandomProblemRequest(
        @NotNull Long chapterId,
        @NotNull Long userId
) {
}

package com.jms.assignment1.api.problem;

import com.jms.assignment1.api.problem.dto.GetProblemHistoryResponse;
import com.jms.assignment1.api.problem.dto.GetRandomProblemRequest;
import com.jms.assignment1.api.problem.dto.GetRandomProblemResponse;
import com.jms.assignment1.api.problem.dto.SubmitAnswerRequest;
import com.jms.assignment1.api.problem.dto.SubmitAnswerResponse;
import com.jms.assignment1.application.problem.GetProblemHistoryUseCase;
import com.jms.assignment1.application.problem.GetRandomProblemUseCase;
import com.jms.assignment1.application.problem.SubmitAnswerUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final GetRandomProblemUseCase getRandomProblemUseCase;
    private final SubmitAnswerUseCase submitAnswerUseCase;
    private final GetProblemHistoryUseCase getProblemHistoryUseCase;

    @PostMapping("/random")
    public GetRandomProblemResponse getRandomProblem(@Valid @RequestBody GetRandomProblemRequest request) {
        return GetRandomProblemResponse.from(getRandomProblemUseCase.execute(request.userId(), request.chapterId()));
    }

    @PostMapping("/{problemId}/submit")
    public SubmitAnswerResponse submitAnswer(
            @PathVariable Long problemId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        return SubmitAnswerResponse.from(submitAnswerUseCase.execute(
                request.userId(), problemId, request.selectedChoices(), request.text()));
    }

    @GetMapping("/{problemId}/history")
    public GetProblemHistoryResponse getProblemHistory(
            @PathVariable Long problemId,
            @RequestParam Long userId) {
        return GetProblemHistoryResponse.from(getProblemHistoryUseCase.execute(userId, problemId));
    }
}

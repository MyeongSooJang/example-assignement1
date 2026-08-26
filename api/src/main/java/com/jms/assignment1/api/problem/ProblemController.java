package com.jms.assignment1.api.problem;

import com.jms.assignment1.api.problem.dto.GetProblemHistoryResponse;
import com.jms.assignment1.api.problem.dto.GetRandomProblemRequest;
import com.jms.assignment1.api.problem.dto.GetRandomProblemResponse;
import com.jms.assignment1.api.problem.dto.SubmitAnswerRequest;
import com.jms.assignment1.api.problem.dto.SubmitAnswerResponse;
import com.jms.assignment1.application.problem.GetProblemHistoryUseCase;
import com.jms.assignment1.application.problem.GetRandomProblemUseCase;
import com.jms.assignment1.application.problem.SubmitAnswerUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "문제 풀이", description = "단원별 문제 풀이 및 풀이 이력 조회 API")
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final GetRandomProblemUseCase getRandomProblemUseCase;
    private final SubmitAnswerUseCase submitAnswerUseCase;
    private final GetProblemHistoryUseCase getProblemHistoryUseCase;

    @Operation(summary = "랜덤 문제 조회", description = "사용자가 아직 풀지 않은 문제를 랜덤으로 1개 반환합니다. 직전에 건너뛴 문제는 제외됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문제 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 단원을 찾을 수 없음"),
            @ApiResponse(responseCode = "204", description = "풀 수 있는 문제 없음")
    })
    @GetMapping("/random")
    public GetRandomProblemResponse getRandomProblem(@Valid GetRandomProblemRequest request) {
        return GetRandomProblemResponse.from(getRandomProblemUseCase.execute(request.userId(), request.chapterId()));
    }

    @Operation(summary = "문제 제출", description = "객관식 또는 주관식 답안을 제출합니다. 정답/부분정답/오답 여부와 해설을 즉시 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "제출 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (answerType과 답안 형식 불일치)"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 문제를 찾을 수 없음")
    })
    @PostMapping("/{problemId}/submit")
    public SubmitAnswerResponse submitAnswer(
            @Parameter(description = "문제 ID", example = "1") @PathVariable Long problemId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        return SubmitAnswerResponse.from(submitAnswerUseCase.execute(
                request.userId(), problemId, request.selectedChoices(), request.text()));
    }

    @Operation(summary = "풀이 이력 조회", description = "사용자가 이전에 풀었던 문제의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "풀이 이력을 찾을 수 없음")
    })
    @GetMapping("/{problemId}/history")
    public GetProblemHistoryResponse getProblemHistory(
            @Parameter(description = "문제 ID", example = "1") @PathVariable Long problemId,
            @Parameter(description = "사용자 ID", example = "1") @RequestParam Long userId) {
        return GetProblemHistoryResponse.from(getProblemHistoryUseCase.execute(userId, problemId));
    }
}

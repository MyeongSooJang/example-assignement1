package com.jms.assignment1.api.problem;

import com.jms.assignment1.api.problem.dto.GetRandomProblemRequest;
import com.jms.assignment1.api.problem.dto.GetRandomProblemResponse;
import com.jms.assignment1.application.problem.GetRandomProblemUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final GetRandomProblemUseCase getRandomProblemUseCase;

    @PostMapping("/random")
    public GetRandomProblemResponse getRandomProblem(@Valid @RequestBody GetRandomProblemRequest request) {
        return GetRandomProblemResponse.from(getRandomProblemUseCase.execute(request.userId(), request.chapterId()));
    }
}

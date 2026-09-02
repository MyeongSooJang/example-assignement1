package com.jms.assignment1.application.problem;

import com.jms.assignment1.application.common.ChapterValidator;
import com.jms.assignment1.application.common.UserValidator;
import com.jms.assignment1.problem.Problem;
import com.jms.assignment1.problem.Problems;
import com.jms.assignment1.repository.ProblemRepository;
import com.jms.assignment1.repository.UserChapterSkipRepository;
import com.jms.assignment1.repository.UserProblemHistoryRepository;
import com.jms.assignment1.service.ProblemCorrectRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class GetRandomProblemService implements GetRandomProblemUseCase {

    private final ProblemRepository problemRepository;
    private final UserProblemHistoryRepository userProblemHistoryRepository;
    private final UserChapterSkipRepository userChapterSkipRepository;
    private final UserValidator userValidator;
    private final ChapterValidator chapterValidator;
    private final ProblemCorrectRateService problemCorrectRateService;

    @Override
    public RandomProblemResult execute(Long userId, Long chapterId) {
        userValidator.validate(userId);
        chapterValidator.validate(chapterId);

        Set<Long> excludedProblemIds = buildExcludedProblemIds(userId, chapterId);
        Problems chapterProblems = problemRepository.findByChapterId(chapterId);
        Problem selectedProblem = chapterProblems.excluding(excludedProblemIds).pickRandom();

        Integer answerCorrectRate = problemCorrectRateService.calculate(selectedProblem.getId());

        return new RandomProblemResult(selectedProblem, answerCorrectRate);
    }

    private Set<Long> buildExcludedProblemIds(Long userId, Long chapterId) {
        Set<Long> excludedProblemIds = new HashSet<>(
                userProblemHistoryRepository.findSolvedProblemIdsByUserIdAndChapterId(userId, chapterId)
        );
        userChapterSkipRepository.findSkippedProblemIdByUserIdAndChapterId(userId, chapterId)
                                 .ifPresent(excludedProblemIds::add);
        return excludedProblemIds;
    }
}

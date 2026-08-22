package com.jms.assignment1.application.problem;

import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.application.common.ChapterValidator;
import com.jms.assignment1.application.common.UserValidator;
import com.jms.assignment1.problem.Problem;
import com.jms.assignment1.repository.ProblemRepository;
import com.jms.assignment1.repository.UserChapterSkipRepository;
import com.jms.assignment1.repository.UserProblemHistoryRepository;
import com.jms.assignment1.service.CorrectRateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class GetRandomProblemService implements GetRandomProblemUseCase {

    private final ProblemRepository problemRepository;
    private final UserProblemHistoryRepository userProblemHistoryRepository;
    private final UserChapterSkipRepository userChapterSkipRepository;
    private final UserValidator userValidator;
    private final ChapterValidator chapterValidator;

    @Override
    public RandomProblemResult execute(Long userId, Long chapterId) {
        validate(userId, chapterId);

        List<Problem> availableProblems = findAvailableProblems(chapterId, excludedProblemIds(userId, chapterId));

        Problem selectedProblem = selectRandomProblem(availableProblems);

        Integer answerCorrectRate = calculateAnswerCorrectRate(selectedProblem);

        return new RandomProblemResult(selectedProblem, answerCorrectRate);
    }

    private void validate(Long userId, Long chapterId) {
        userValidator.validate(userId);
        chapterValidator.validate(chapterId);
    }

    private Set<Long> excludedProblemIds(Long userId, Long chapterId) {
        Set<Long> excludedProblemIds = solvedProblemIds(userId, chapterId);
        skippedProblemId(userId, chapterId).ifPresent(excludedProblemIds::add);
        return excludedProblemIds;
    }

    private Set<Long> solvedProblemIds(Long userId, Long chapterId) {
        return new HashSet<>(userProblemHistoryRepository.findSolvedProblemIdsByUserIdAndChapterId(userId, chapterId));
    }

    private Optional<Long> skippedProblemId(Long userId, Long chapterId) {
        return userChapterSkipRepository.findSkippedProblemIdByUserIdAndChapterId(userId, chapterId);
    }

    private List<Problem> findAvailableProblems(Long chapterId, Set<Long> excludedProblemIds) {
        List<Problem> availableProblems = problemRepository.findByChapterId(chapterId).stream()
                                                            .filter(problem -> !excludedProblemIds.contains(problem.getId()))
                                                            .toList();

        if (availableProblems.isEmpty()) {
            throw new NoSuchElementException("풀 수 있는 문제가 없습니다.");
        }

        return availableProblems;
    }

    private Problem selectRandomProblem(List<Problem> availableProblems) {
        return availableProblems.get((int) (Math.random() * availableProblems.size()));
    }

    private Integer calculateAnswerCorrectRate(Problem selectedProblem) {
        List<AnswerStatus> answerStatuses = userProblemHistoryRepository.findAnswerStatusesByProblemId(selectedProblem.getId());
        long correctCount = answerStatuses.stream().filter(status -> status == AnswerStatus.CORRECT).count();
        return CorrectRateCalculator.calculate(answerStatuses.size(), correctCount);
    }
}

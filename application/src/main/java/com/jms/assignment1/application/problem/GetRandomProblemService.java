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
        userValidator.validate(userId);
        chapterValidator.validate(chapterId);

        Set<Long> excludedProblemIds = excludedProblemIds(userId, chapterId);

        List<Problem> candidates = problemRepository.findByChapterId(chapterId).stream()
                                                    .filter(problem -> !excludedProblemIds.contains(problem.getId()))
                                                    .toList();

        if (candidates.isEmpty()) {
            throw new NoSuchElementException("풀 수 있는 문제가 없습니다.");
        }

        Problem selectedProblem = candidates.get((int) (Math.random() * candidates.size()));

        List<AnswerStatus> answerStatuses = userProblemHistoryRepository.findAnswerStatusesByProblemId(selectedProblem.getId());

        long correctCount = answerStatuses.stream().filter(status -> status == AnswerStatus.CORRECT).count();

        Integer answerCorrectRate = CorrectRateCalculator.calculate(answerStatuses.size(), correctCount);

        return new RandomProblemResult(selectedProblem, answerCorrectRate);
    }

    private Set<Long> excludedProblemIds(Long userId, Long chapterId) {
        Set<Long> excludedProblemIds = new HashSet<>(userProblemHistoryRepository.findSolvedProblemIdsByUserIdAndChapterId(userId, chapterId));
        userChapterSkipRepository.findSkippedProblemIdByUserIdAndChapterId(userId, chapterId)
                                 .ifPresent(excludedProblemIds::add);
        return excludedProblemIds;
    }
}

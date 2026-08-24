package com.jms.assignment1.application.problem;

import com.jms.assignment1.answer.Answer;
import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.application.common.UserValidator;
import com.jms.assignment1.exception.ProblemNotFoundException;
import com.jms.assignment1.history.UserProblemHistory;
import com.jms.assignment1.problem.Problem;
import com.jms.assignment1.repository.ProblemRepository;
import com.jms.assignment1.repository.UserProblemHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmitAnswerService implements SubmitAnswerUseCase {

    private final UserValidator userValidator;
    private final ProblemRepository problemRepository;
    private final UserProblemHistoryRepository userProblemHistoryRepository;

    @Override
    public SubmitAnswerResult execute(Long userId, Long problemId, List<Integer> selectedChoices, String text) {
        userValidator.validate(userId);

        Problem problem = findProblem(problemId);
        AnswerEvaluation answerEvaluation = evaluate(problem, selectedChoices, text);

        UserProblemHistory history = upsertHistory(userId, problemId, answerEvaluation.getAnswerStatus(), answerEvaluation.getAnswer());
        userProblemHistoryRepository.saveOrUpdate(history);

        return new SubmitAnswerResult(answerEvaluation.getAnswerStatus(), problem.getSolution());
    }

    private Problem findProblem(Long problemId) {
        return problemRepository.findById(problemId)
                                 .orElseThrow(() -> new ProblemNotFoundException(problemId));
    }

    private AnswerEvaluation evaluate(Problem problem, List<Integer> selectedChoices, String text) {
        Answer answer = problem.createAnswer(selectedChoices, text);
        AnswerStatus answerStatus = problem.evaluate(answer);
        return new AnswerEvaluation(answerStatus, answer);
    }

    private UserProblemHistory upsertHistory(Long userId, Long problemId, AnswerStatus answerStatus, Answer answer) {
        return userProblemHistoryRepository.findByUserIdAndProblemId(userId, problemId)
                                            .map(found -> found.update(answerStatus, answer))
                                            .orElseGet(() -> UserProblemHistory.create(userId, problemId, answerStatus, answer));
    }
}

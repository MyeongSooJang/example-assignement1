package com.jms.assignment1.application.problem;

import com.jms.assignment1.answer.Answer;
import com.jms.assignment1.answer.AnswerStatus;
import com.jms.assignment1.answer.MultipleChoiceAnswer;
import com.jms.assignment1.answer.ShortAnswer;
import com.jms.assignment1.application.common.UserValidator;
import com.jms.assignment1.exception.ProblemNotFoundException;
import com.jms.assignment1.history.UserProblemHistory;
import com.jms.assignment1.problem.MultipleChoiceProblem;
import com.jms.assignment1.problem.Problem;
import com.jms.assignment1.problem.ShortAnswerProblem;
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

        UserProblemHistory history = resolveHistory(userId, problemId, answerEvaluation.getAnswerStatus(), answerEvaluation.getAnswer());
        userProblemHistoryRepository.saveOrUpdate(history);

        return new SubmitAnswerResult(answerEvaluation.getAnswerStatus(), problem.getSolution());
    }

    private Problem findProblem(Long problemId) {
        return problemRepository.findById(problemId)
                                 .orElseThrow(() -> new ProblemNotFoundException(problemId));
    }

    private AnswerEvaluation evaluate(Problem problem, List<Integer> selectedChoices, String text) {
        if (problem instanceof MultipleChoiceProblem multipleChoiceProblem) {
            MultipleChoiceAnswer multipleChoiceAnswer = new MultipleChoiceAnswer(selectedChoices);
            return new AnswerEvaluation(multipleChoiceProblem.evaluate(multipleChoiceAnswer), multipleChoiceAnswer);
        }
        if (problem instanceof ShortAnswerProblem shortAnswerProblem) {
            ShortAnswer shortAnswer = new ShortAnswer(text);
            return new AnswerEvaluation(shortAnswerProblem.evaluate(shortAnswer), shortAnswer);
        }
        throw new IllegalStateException("알 수 없는 문제 타입입니다: " + problem.getClass());
    }

    private UserProblemHistory resolveHistory(Long userId, Long problemId, AnswerStatus answerStatus, Answer answer) {
        return userProblemHistoryRepository.findByUserIdAndProblemId(userId, problemId)
                                            .map(found -> found.update(answerStatus, answer))
                                            .orElseGet(() -> UserProblemHistory.create(userId, problemId, answerStatus, answer));
    }
}

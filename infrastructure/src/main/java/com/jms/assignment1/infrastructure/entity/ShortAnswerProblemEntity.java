package com.jms.assignment1.infrastructure.entity;

import com.jms.assignment1.problem.Problem;
import com.jms.assignment1.problem.ShortAnswerProblem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "short_answer_problems")
@DiscriminatorValue("SHORT_ANSWER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortAnswerProblemEntity extends ProblemEntity {

    @Column(nullable = false)
    private String correctAnswer;

    @Override
    public Problem toDomain() {
        return new ShortAnswerProblem(getId(), getChapterId(), getContent(), getSolution(), correctAnswer);
    }
}

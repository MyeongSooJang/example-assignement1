package com.jms.assignment1.infrastructure.entity;

import com.jms.assignment1.infrastructure.entity.converter.IntegerListConverter;
import com.jms.assignment1.infrastructure.entity.converter.StringListConverter;
import com.jms.assignment1.problem.MultipleChoiceProblem;
import com.jms.assignment1.problem.Problem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "multiple_choice_problems")
@DiscriminatorValue("MULTIPLE_CHOICE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MultipleChoiceProblemEntity extends ProblemEntity {

    @Convert(converter = StringListConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private List<String> choices;

    @Convert(converter = IntegerListConverter.class)
    @Column(nullable = false)
    private List<Integer> correctAnswers;

    @Override
    public Problem toDomain() {
        return new MultipleChoiceProblem(getId(), getChapterId(), getContent(), getSolution(), choices, correctAnswers);
    }
}

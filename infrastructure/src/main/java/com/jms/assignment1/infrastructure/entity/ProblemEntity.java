package com.jms.assignment1.infrastructure.entity;

import com.jms.assignment1.problem.Problem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "problems")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "problem_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ProblemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long chapterId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String solution;

    public abstract Problem toDomain();
}

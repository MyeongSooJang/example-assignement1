package com.jms.assignment1.repository;

import com.jms.assignment1.problem.Problem;
import com.jms.assignment1.problem.Problems;

import java.util.Optional;

public interface ProblemRepository {

    Optional<Problem> findById(Long id);

    Problems findByChapterId(Long chapterId);
}

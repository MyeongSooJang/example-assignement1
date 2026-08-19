package com.jms.assignment1.repository;

import com.jms.assignment1.problem.Problem;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository {

    Optional<Problem> findById(Long id);

    List<Problem> findByChapterId(Long chapterId);
}

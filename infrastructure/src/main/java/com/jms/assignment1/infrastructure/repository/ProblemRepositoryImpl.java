package com.jms.assignment1.infrastructure.repository;

import com.jms.assignment1.problem.Problem;
import com.jms.assignment1.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProblemRepositoryImpl implements ProblemRepository {

    private final ProblemJpaRepository problemJpaRepository;

    @Override
    public Optional<Problem> findById(Long id) {
        return problemJpaRepository.findById(id).map(entity -> entity.toDomain());
    }

    @Override
    public List<Problem> findByChapterId(Long chapterId) {
        return problemJpaRepository.findByChapterId(chapterId).stream()
                                   .map(entity -> entity.toDomain())
                                   .toList();
    }
}

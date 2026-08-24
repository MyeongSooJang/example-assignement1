package com.jms.assignment1.infrastructure.repository;

import com.jms.assignment1.chapter.Chapter;
import com.jms.assignment1.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChapterRepositoryImpl implements ChapterRepository {

    private final ChapterJpaRepository chapterJpaRepository;

    @Override
    public Optional<Chapter> findById(Long id) {
        return chapterJpaRepository.findById(id).map(entity -> entity.toDomain());
    }
}

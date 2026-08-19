package com.jms.assignment1.repository;

import com.jms.assignment1.chapter.Chapter;

import java.util.Optional;

public interface ChapterRepository {

    Optional<Chapter> findById(Long id);
}

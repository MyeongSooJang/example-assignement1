package com.jms.assignment1.application.common;

import com.jms.assignment1.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class ChapterValidator {

    private final ChapterRepository chapterRepository;

    public void validate(Long chapterId) {
        chapterRepository.findById(chapterId)
                         .orElseThrow(() -> new NoSuchElementException("존재하지 않는 단원입니다. chapterId: " + chapterId));
    }
}

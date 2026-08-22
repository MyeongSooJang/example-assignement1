package com.jms.assignment1.application.common;

import com.jms.assignment1.exception.ChapterNotFoundException;
import com.jms.assignment1.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChapterValidator {

    private final ChapterRepository chapterRepository;

    public void validate(Long chapterId) {
        chapterRepository.findById(chapterId)
                         .orElseThrow(() -> new ChapterNotFoundException(chapterId));
    }
}

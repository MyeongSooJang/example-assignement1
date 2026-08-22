package com.jms.assignment1.exception;

public class ChapterNotFoundException extends DomainException {

    public ChapterNotFoundException(Long chapterId) {
        super(ErrorMessage.CHAPTER_NOT_FOUND.format(chapterId));
    }
}

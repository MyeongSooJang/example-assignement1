package com.jms.assignment1.api.exception;

import com.jms.assignment1.exception.ChapterNotFoundException;
import com.jms.assignment1.exception.NoAvailableProblemException;
import com.jms.assignment1.exception.ProblemHistoryNotFoundException;
import com.jms.assignment1.exception.ProblemNotFoundException;
import com.jms.assignment1.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoAvailableProblemException.class)
    public ResponseEntity<Void> handleNoAvailableProblem(NoAvailableProblemException exception) {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            ChapterNotFoundException.class,
            ProblemNotFoundException.class,
            ProblemHistoryNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                                   .findFirst()
                                   .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                                   .orElse("잘못된 요청입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
    }
}

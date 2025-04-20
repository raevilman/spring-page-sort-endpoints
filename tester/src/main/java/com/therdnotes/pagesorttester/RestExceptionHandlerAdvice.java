package com.therdnotes.pagesorttester;

import com.therdnotes.springpagesort.PageSortValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PageSortValidationException.class)
    public ResponseEntity<?> handlePageSortValidationException(PageSortValidationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "ConsumerError: "+ex.getMessage());
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        ProblemDetail problemDetail;

        if (e instanceof IllegalArgumentException rse) {
            String requestId = UUID.randomUUID().toString();
            log.error("IllegalArgumentException: " + rse.getMessage() + ". Request ID: " + requestId, e);
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "IllegalArgumentException: " + rse.getMessage() + ". Request ID: " + requestId);
        } else {
            String requestId = UUID.randomUUID().toString();
            log.error("Something went wrong. Request ID: " + requestId, e);
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Request ID: " + requestId);
        }

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
}

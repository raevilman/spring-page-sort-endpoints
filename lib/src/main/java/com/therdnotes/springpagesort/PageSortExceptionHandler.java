package com.therdnotes.springpagesort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Default exception handler for PageSortValidationException.
 * This handler can be enabled/disabled via application properties:
 * com.therdnotes.springpagesort.exception-handling.enabled=true|false (defaults to true)
 */
@RestControllerAdvice
@Order(1) // Higher priority (lower number)
@ConditionalOnProperty(
        name = "com.therdnotes.springpagesort.exception-handling.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Slf4j
public class PageSortExceptionHandler {

    public PageSortExceptionHandler() {
        log.info("PageSortExceptionHandler is enabled");
    }

    @ExceptionHandler(PageSortValidationException.class)
    public ProblemDetail handlePageSortValidationException(PageSortValidationException ex) {
        log.error("PageSortValidationException: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());

        if (ex.getField() != null) {
            problemDetail.setProperty("field", ex.getField());
        }
        if (ex.getValue() != null) {
            problemDetail.setProperty("value", ex.getValue());
        }

        return problemDetail;
    }
}
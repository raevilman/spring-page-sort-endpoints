package com.therdnotes.springpagesort;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when validation of page/sort parameters fails.
 * Extends ResponseStatusException to work seamlessly with existing exception handlers.
 */
@Getter
public class PageSortValidationException extends ResponseStatusException {
    private final String field;
    private final String value;

    public PageSortValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
        this.field = null;
        this.value = null;
    }

    public PageSortValidationException(String message, String field, String value) {
        super(HttpStatus.BAD_REQUEST, message);
        this.field = field;
        this.value = value;
    }

    @Override
    @NonNull
    public String getMessage() {
        return getReason() != null ? getReason() : "No reason provided";
    }

}
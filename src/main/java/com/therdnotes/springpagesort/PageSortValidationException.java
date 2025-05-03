package com.therdnotes.springpagesort;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when validation of pagination and sorting parameters fails.
 * This exception is used by the {@link PageSortArgumentResolver} to handle invalid
 * request parameters for pagination and sorting.
 * <p>
 * Extends {@link ResponseStatusException} with a BAD_REQUEST (400) status
 * to seamlessly integrate with Spring's exception handling mechanism.
 * <p>
 * This exception can optionally capture which specific field failed validation
 * and what invalid value was provided, making it easier to generate detailed
 * error responses.
 *
 * @see PageSortArgumentResolver
 * @see PageSortConfig
 */
@Getter
public class PageSortValidationException extends ResponseStatusException {
    /**
     * The name of the field that failed validation (e.g., "page", "size", "sortBy", "sortDir").
     * May be null if the specific field is not identified.
     */
    private final String field;

    /**
     * The invalid value that was provided for the field.
     * May be null if the specific value is not captured.
     */
    private final String value;

    /**
     * Constructs a new exception with the specified error message.
     * Field and value information is not included in this constructor.
     *
     * @param message detailed error message explaining the validation failure
     */
    public PageSortValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
        this.field = null;
        this.value = null;
    }

    /**
     * Constructs a new exception with detailed information about the validation failure.
     *
     * @param message detailed error message explaining the validation failure
     * @param field the name of the parameter that failed validation (e.g., "page", "size")
     * @param value the invalid value that was provided
     */
    public PageSortValidationException(String message, String field, String value) {
        super(HttpStatus.BAD_REQUEST, message);
        this.field = field;
        this.value = value;
    }

    /**
     * Returns the reason for this exception.
     * Overrides the parent method to ensure a non-null message is always returned.
     *
     * @return the reason for this exception, or a default message if no reason was provided
     */
    @Override
    @NonNull
    public String getMessage() {
        return getReason() != null ? getReason() : "No reason provided";
    }
}
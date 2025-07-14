package com.therdnotes.springpagesort;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to configure and validate pagination and sorting query parameters.
 * <p>
 * This annotation is used on controller methods that accept a {@link PageSortRequest}
 * parameter. It defines constraints and default values for offset, limit,
 * and valid sort fields.
 * <p>
 * When applied to a controller method, the {@link PageSortArgumentResolver} will:
 * <ul>
 *   <li>Validate that offset is not less than {@code minOffset}</li>
 *   <li>Validate that limit is between {@code minLimit} and {@code maxLimit}</li>
 *   <li>Verify that the requested sort field is in the list of {@code validSortFields}</li>
 *   <li>Apply default values when parameters are missing or invalid</li>
 * </ul>
 * <p>
 * If validation fails, the resolver will return a 400 Bad Request with appropriate error details.
 *
 * @see PageSortRequest
 * @see PageSortArgumentResolver
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageSortConfig {
    /**
     * Minimum allowed offset (0-based indexing).
     * Requests with offset less than this value will result in a 400 Bad Request.
     *
     * @return minimum offset (default: 0)
     */
    int minOffset() default 0;

    /**
     * Default offset to use when not specified in the request.
     *
     * @return default offset (default: 0)
     */
    int defaultOffset() default 0;

    /**
     * Minimum allowed limit.
     * Requests with limit less than this value will result in a 400 Bad Request.
     *
     * @return minimum limit (default: 1)
     */
    int minLimit() default 1;

    /**
     * Maximum allowed limit.
     * Requests with limit greater than this value will result in a 400 Bad Request.
     *
     * @return maximum limit (default: 100)
     */
    int maxLimit() default 100;

    /**
     * Default limit to use when not specified in the request.
     *
     * @return default limit (default: 25)
     */
    int defaultLimit() default 25;

    /**
     * List of field names that can be used for sorting.
     * Requests with a sort field not in this list will result in a 400 Bad Request.
     * An empty array means no sort fields are allowed.
     *
     * @return array of valid sort field names (default: empty array)
     */
    String[] validSortFields() default {};

    /**
     * Default sort field to use when not specified in the request.
     * The value must be one of the fields specified in {@code validSortFields}.
     *
     * @return default sort field
     */
    String defaultSortBy() default "";
}
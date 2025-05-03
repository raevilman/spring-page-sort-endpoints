package com.therdnotes.springpagesort;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to configure and validate pagination and sorting query parameters.
 * <p>
 * This annotation is used on controller methods that accept a {@link PageSortRequest}
 * parameter. It defines constraints and default values for page number, page size,
 * and valid sort fields.
 * <p>
 * When applied to a controller method, the {@link PageSortArgumentResolver} will:
 * <ul>
 *   <li>Validate that page number is not less than {@code minPage}</li>
 *   <li>Validate that page size is between {@code minSize} and {@code maxSize}</li>
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
     * Minimum allowed page number (0-based indexing).
     * Requests with page less than this value will result in a 400 Bad Request.
     *
     * @return minimum page number (default: 0)
     */
    int minPage() default 0;

    /**
     * Default page number to use when not specified in the request.
     *
     * @return default page number (default: 0)
     */
    int defaultPage() default 0;

    /**
     * Minimum allowed page size.
     * Requests with size less than this value will result in a 400 Bad Request.
     *
     * @return minimum page size (default: 1)
     */
    int minSize() default 1;

    /**
     * Maximum allowed page size.
     * Requests with size greater than this value will result in a 400 Bad Request.
     *
     * @return maximum page size (default: 100)
     */
    int maxSize() default 100;

    /**
     * Default page size to use when not specified in the request.
     *
     * @return default page size (default: 25)
     */
    int defaultSize() default 25;

    /**
     * List of field names that can be used for sorting.
     * Requests with a sort field not in this list will result in a 400 Bad Request.
     * An empty array means no sort fields are allowed.
     *
     * @return array of valid sort field names (default: empty array)
     */
    String[] validSortFields() default {};
}
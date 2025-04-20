package com.therdnotes.springpagesort;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to validate page and sort query parameters.
 * Used on controller methods that accept pagination and sorting parameters.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageSortConfig {
    /**
     * Minimum page number (default: 0)
     */
    int minPage() default 0;

    /**
     * Default page number when not specified or invalid (default: 0)
     */
    int defaultPage() default 0;

    /**
     * Minimum size/limit per page (default: 1)
     */
    int minSize() default 1;

    /**
     * Maximum size/limit per page (default: 100)
     */
    int maxSize() default 100;

    /**
     * Default page size when not specified or invalid (default: 25)
     */
    int defaultSize() default 25;

    /**
     * Valid field names that can be used for sorting
     */
    String[] validSortFields() default {};
}
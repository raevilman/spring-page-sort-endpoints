package com.therdnotes.springpagesort;

import java.util.Set;

/**
 * Compact constructor that applies validation and default values to request parameters.
 * <p>
 * This constructor ensures:
 * <ul>
 *   <li>Page number is not negative (defaults to {@value #PAGE_DEFAULT} if negative)</li>
 *   <li>Page size is positive (defaults to {@value #SIZE_DEFAULT} if zero or negative)</li>
 *   <li>Sort direction is valid (defaults to "asc" if null, blank, or invalid)</li>
 * </ul>
 * <p>
 * The constructor normalizes the sort direction to lowercase when valid.
 *
 * @param page    The page number (0-based)
 * @param size    The number of items per page
 * @param sortBy  The field to sort by
 * @param sortDir The direction of sorting (asc or desc, case-insensitive)
 */
public record PageSortRequest(
        int page,
        int size,
        String sortBy,
        // Don't make enums for sort direction as it would require custom conversion from query params. Lets keep it simple.
        String sortDir
) {
    private static final Set<String> VALID_SORT_DIRECTIONS = Set.of("asc", "desc");
    private static final int PAGE_DEFAULT = 0;
    private static final int SIZE_DEFAULT = 25;
    private static final String SORT_DIR_DEFAULT = "asc";

    /**
     * Compact constructor that applies validation and default values to request parameters.
     * <p>
     * This constructor ensures:
     * <ul>
     *   <li>Page number is not negative (defaults to {@value #PAGE_DEFAULT} if negative)</li>
     *   <li>Page size is positive (defaults to {@value #SIZE_DEFAULT} if zero or negative)</li>
     *   <li>Sort direction is valid (defaults to "asc" if null, blank, or invalid)</li>
     * </ul>
     * <p>
     * The constructor normalizes the sort direction to lowercase when valid.
     *
     * @param page    The page number (0-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The direction of sorting (asc or desc, case-insensitive)
     */
    public PageSortRequest {
        // Default values
        if (page < 0) page = PAGE_DEFAULT;
        if (size <= 0) size = SIZE_DEFAULT;
        // Default/validation for sortDir
        if (sortDir == null || sortDir.isBlank() || !isValidSortDirection(sortDir)) {
            sortDir = "asc";
        } else {
            sortDir = sortDir.toLowerCase();
        }
    }

    /**
     * Default constructor that creates a PageSortRequest with default values.
     * <p>
     * Default values are:
     * <ul>
     *   <li>page = {@value #PAGE_DEFAULT}</li>
     *   <li>size = {@value #SIZE_DEFAULT}</li>
     *   <li>sortBy = null</li>
     *   <li>sortDir = {@value #SORT_DIR_DEFAULT}</li>
     * </ul>
     */
    public PageSortRequest() {
        this(PAGE_DEFAULT, SIZE_DEFAULT, null, SORT_DIR_DEFAULT);
    }

    /**
     * Validates whether the provided sort direction is valid.
     * <p>
     * A valid sort direction is either "asc" or "desc" (case-insensitive).
     *
     * @param direction the sort direction to validate
     * @return true if the direction is valid, false otherwise
     */
    private static boolean isValidSortDirection(String direction) {
        return VALID_SORT_DIRECTIONS.contains(direction.toLowerCase());
    }

    /**
     * Determines if the sort direction is ascending.
     *
     * @return true if sort direction is "asc" (case-insensitive), false otherwise
     */
    public boolean isAscending() {
        return "asc".equalsIgnoreCase(sortDir);
    }

    /**
     * Returns the sort direction in SQL-compatible format.
     * <p>
     * Converts the sort direction to the SQL syntax "ASC" or "DESC".
     *
     * @return "ASC" if sort direction is ascending, "DESC" otherwise
     */
    public String getSqlSortDirection() {
        return isAscending() ? "ASC" : "DESC";
    }
}

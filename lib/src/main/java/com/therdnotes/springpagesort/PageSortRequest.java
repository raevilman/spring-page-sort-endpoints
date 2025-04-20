package com.therdnotes.springpagesort;

import java.util.Set;

/**
 * <p>
 * Use this in combination with {@link PageSortConfig}
 * to define and validate values.
 * </p>
 *
 * <p>
 * This object is resolved at runtime by the {@link PageSortArgumentResolver}
 * </p>
 *
 * <p>
 * Represents a request for paginated and sorted data.
 * This class encapsulates pagination and sorting parameters for API requests.
 * It provides default values and validation for the parameters.
 * </p>
 *
 * @param page    The page number (0-based).
 * @param size    The number of items per page.
 * @param sortBy  The field to sort by.
 * @param sortDir The direction of sorting (asc or desc).
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

    // Compact constructor with defaults
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

    // Default constructor with defaults
    public PageSortRequest() {
        this(PAGE_DEFAULT, SIZE_DEFAULT, null, SORT_DIR_DEFAULT);
    }

    private static boolean isValidSortDirection(String direction) {
        return VALID_SORT_DIRECTIONS.contains(direction.toLowerCase());
    }

    // Utility methods
    public boolean isAscending() {
        return "asc".equalsIgnoreCase(sortDir);
    }

    public String getSqlSortDirection() {
        return isAscending() ? "ASC" : "DESC";
    }
}

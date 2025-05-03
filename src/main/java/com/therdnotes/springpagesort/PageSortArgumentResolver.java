package com.therdnotes.springpagesort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom argument resolver for handling pagination and sorting parameters in controller methods.
 * <p>
 * This resolver automatically converts HTTP request parameters into {@link PageSortRequest} objects
 * for controller methods that accept this type as a parameter. The resolver performs validation
 * based on constraints defined in the {@link PageSortConfig} annotation on the controller method.
 * <p>
 * The resolver handles the following request parameters:
 * <ul>
 *   <li>{@code page} - The page number (0-based)</li>
 *   <li>{@code size} - The number of items per page</li>
 *   <li>{@code sortBy} - The field to sort by</li>
 *   <li>{@code sortDir} - The sort direction ("asc" or "desc")</li>
 * </ul>
 * <p>
 * If validation fails, a {@link PageSortValidationException} is thrown with an appropriate
 * error message. This exception is automatically converted to an HTTP 400 Bad Request response
 * by the default exception handler.
 *
 * @see PageSortRequest
 * @see PageSortConfig
 * @see PageSortValidationException
 * @see WebMvcConfig
 */
@Component
@Slf4j
public class PageSortArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * Constructs a new PageSortArgumentResolver with default settings.
     * Initializes logging for tracking resolver activities.
     */
    public PageSortArgumentResolver() {
        log.info("PageSortArgumentResolver initialized");
    }

    /**
     * Determines if this resolver supports the given method parameter.
     * <p>
     * This resolver only supports parameters of type {@link PageSortRequest}.
     *
     * @param parameter the method parameter to check
     * @return true if the parameter is of type {@link PageSortRequest}, false otherwise
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean supported = parameter.getParameterType().equals(PageSortRequest.class);
        log.debug("PageSortArgumentResolver.supportsParameter: {} for parameter type {}",
                supported, parameter.getParameterType().getSimpleName());
        return supported;
    }

    /**
     * Resolves the argument value for a supported parameter.
     * <p>
     * This method extracts pagination and sorting parameters from the request,
     * validates them according to the constraints defined in the {@link PageSortConfig}
     * annotation (if present), and creates a {@link PageSortRequest} object with the
     * extracted and validated values.
     *
     * @param parameter the method parameter to resolve
     * @param mavContainer the ModelAndViewContainer for the current request
     * @param webRequest the current request
     * @param binderFactory the factory to create data binders
     * @return a {@link PageSortRequest} object populated with validated pagination and sorting parameters
     * @throws PageSortValidationException if validation of any parameter fails
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        log.debug("Resolving PageSortRequest argument for parameter: {}", parameter.getParameterName());

        // Extract annotation from method if present
        Method method = parameter.getMethod();
        PageSortConfig pageSortConfig = method != null ?
                method.getAnnotation(PageSortConfig.class) : null;

        if (pageSortConfig != null) {
            log.debug("Found @PageSortConfig annotation on method: {}", method.getName());
            log.trace("PageSortConfig values: defaultPage={}, defaultSize={}, minPage={}, minSize={}, maxSize={}, validSortFields={}",
                    pageSortConfig.defaultPage(), pageSortConfig.defaultSize(),
                    pageSortConfig.minPage(), pageSortConfig.minSize(),
                    pageSortConfig.maxSize(), Arrays.toString(pageSortConfig.validSortFields()));
        } else {
            log.debug("No @PageSortConfig annotation found, using default values");
        }

        // Default values
        int defaultPage = pageSortConfig != null ? pageSortConfig.defaultPage() : 0;
        int defaultSize = pageSortConfig != null ? pageSortConfig.defaultSize() : 25;

        log.debug("Using defaults: defaultPage={}, defaultSize={}", defaultPage, defaultSize);

        // Parse request parameters
        String pageParam = webRequest.getParameter("page");
        String sizeParam = webRequest.getParameter("size");
        String sortByParam = webRequest.getParameter("sortBy");
        String sortDirParam = webRequest.getParameter("sortDir");

        log.debug("Request parameters: page={}, size={}, sortBy={}, sortDir={}",
                pageParam, sizeParam, sortByParam, sortDirParam);

        int page = defaultPage;
        int size = defaultSize;

        try {
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
                log.debug("Parsed page parameter: {}", page);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid page parameter: '{}'.", pageParam);
            throw new PageSortValidationException("Invalid page parameter: " + pageParam);
        }

        try {
            if (sizeParam != null && !sizeParam.isEmpty()) {
                size = Integer.parseInt(sizeParam);
                log.debug("Parsed size parameter: {}", size);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid size parameter: '{}'.", sizeParam);
            throw new PageSortValidationException("Invalid size parameter: " + sizeParam);
        }

        // Apply validation if the annotation is present
        if (pageSortConfig != null) {
            log.debug("Validating parameters using @PageSortConfig constraints");
            try {
                validatePage(page, pageSortConfig.minPage());
                validateSize(size, pageSortConfig.minSize(), pageSortConfig.maxSize());
                validateSortBy(sortByParam, pageSortConfig.validSortFields());
                validateSortDir(sortDirParam);
                log.debug("All pagination parameters are valid");
            } catch (PageSortValidationException e) {
                log.error("Validation error: {}", e.getMessage());
                throw e;
            }
        }

        PageSortRequest request = new PageSortRequest(page, size, sortByParam, sortDirParam);
        log.info("Created PageSortRequest: page={}, size={}, sortBy={}, sortDir={}",
                request.page(), request.size(), request.sortBy(), request.sortDir());
        return request;
    }

    /**
     * Validates that the page number is not less than the minimum allowed value.
     *
     * @param page the page number to validate
     * @param minPage the minimum allowed page number
     * @throws PageSortValidationException if the page number is less than the minimum
     */
    private void validatePage(int page, int minPage) {
        log.trace("Validating page={} against minPage={}", page, minPage);
        if (page < minPage) {
            log.warn("Invalid page number: {} is less than minimum: {}", page, minPage);
            throw new PageSortValidationException("Page number cannot be less than " + minPage);
        }
    }

    /**
     * Validates that the page size is within the allowed range.
     *
     * @param size the page size to validate
     * @param minSize the minimum allowed page size
     * @param maxSize the maximum allowed page size
     * @throws PageSortValidationException if the page size is outside the allowed range
     */
    private void validateSize(int size, int minSize, int maxSize) {
        log.trace("Validating size={} against minSize={}, maxSize={}", size, minSize, maxSize);
        if (size < minSize) {
            log.warn("Invalid page size: {} is less than minimum: {}", size, minSize);
            throw new PageSortValidationException("Page size cannot be less than " + minSize);
        }
        if (size > maxSize) {
            log.warn("Invalid page size: {} is greater than maximum: {}", size, maxSize);
            throw new PageSortValidationException("Page size cannot be greater than " + maxSize);
        }
    }

    /**
     * Validates that the sort field is allowed.
     * <p>
     * If the validSortFields array is empty, no sorting is allowed.
     * If the validSortFields array is not empty, the sortBy parameter must be one of the allowed values.
     *
     * @param sortBy the sort field to validate
     * @param validSortFields array of allowed sort fields
     * @throws PageSortValidationException if sorting is not allowed or the sort field is invalid
     */
    private void validateSortBy(String sortBy, String[] validSortFields) {
        log.trace("Validating sortBy={} against validSortFields={}", sortBy, Arrays.toString(validSortFields));

        if (sortBy != null && !sortBy.isBlank()) {
            log.debug("Sort field provided: {}", sortBy);
            // If validSortFields is empty, no sorting is allowed
            if (validSortFields.length == 0) {
                log.warn("Sorting is not allowed but sortBy parameter was provided: {}", sortBy);
                throw new PageSortValidationException("Sorting is not allowed for this resource");
            }
            // If validSortFields is specified, sortBy must be one of them
            else {
                Set<String> validFieldsSet = Arrays.stream(validSortFields).collect(Collectors.toSet());
                if (!validFieldsSet.contains(sortBy)) {
                    log.warn("Invalid sort field: {} not in allowed fields: {}", sortBy, validFieldsSet);
                    throw new PageSortValidationException(
                            "Invalid sort field: " + sortBy + ". Valid options are: " + String.join(", ", validFieldsSet));
                }
                log.debug("Sort field '{}' is valid", sortBy);
            }
        } else {
            log.debug("No sort field provided");
        }
    }

    /**
     * Validates that the sort direction is valid (either "asc" or "desc", case-insensitive).
     *
     * @param sortDir the sort direction to validate
     * @throws PageSortValidationException if the sort direction is not one of the allowed values
     */
    private void validateSortDir(String sortDir) {
        log.trace("Validating sortDir={}", sortDir);
        if (sortDir != null && !sortDir.isBlank() &&
                !("asc".equalsIgnoreCase(sortDir) || "desc".equalsIgnoreCase(sortDir))) {
            log.warn("Invalid sort direction: {}", sortDir);
            throw new PageSortValidationException("Invalid sort direction: " + sortDir + ". Valid options are: asc, desc");
        }
        if (sortDir != null && !sortDir.isBlank()) {
            log.debug("Sort direction '{}' is valid", sortDir);
        }
    }
}
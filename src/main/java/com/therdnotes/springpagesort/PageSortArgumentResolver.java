package com.therdnotes.springpagesort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
 *   <li>{@code offset} - The offset (0-based)</li>
 *   <li>{@code limit} - The number of items to return</li>
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
     * @param parameter     the method parameter to resolve
     * @param mavContainer  the ModelAndViewContainer for the current request
     * @param webRequest    the current request
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

            log.trace("PageSortConfig values: defaultOffset={}, defaultLimit={}, minOffset={}, minLimit={}, maxLimit={}, defaultSortBy={}, validSortFields={}",
                    pageSortConfig.defaultOffset(), pageSortConfig.defaultLimit(),
                    pageSortConfig.minOffset(), pageSortConfig.minLimit(),
                    pageSortConfig.maxLimit(), pageSortConfig.defaultSortBy(),

                    Arrays.toString(pageSortConfig.validSortFields()));
        } else {
            log.debug("No @PageSortConfig annotation found, using default values");
        }

        // Default values

        int defaultOffset = pageSortConfig != null ? pageSortConfig.defaultOffset() : 0;
        int defaultLimit = pageSortConfig != null ? pageSortConfig.defaultLimit() : 25;
        String defaultSortBy = pageSortConfig != null ? pageSortConfig.defaultSortBy() : null;

        log.debug("Using defaults: defaultOffset={}, defaultLimit={}, defaultSortBy={}",
                defaultOffset, defaultLimit, defaultSortBy);

        // Parse request parameters
        String offsetStr = webRequest.getParameter("offset");
        String limitStr = webRequest.getParameter("limit");
        String sortBy = webRequest.getParameter("sortBy");
        String sortDir = webRequest.getParameter("sortDir");

        log.debug("Request parameters: offset={}, limit={}, sortBy={}, sortDir={}",
                offsetStr, limitStr, sortBy, sortDir);

        int offset = defaultOffset;
        int limit = defaultLimit;

        // Apply defaultSortBy when sortByParam is not provided
        if (!StringUtils.hasText(sortBy) && StringUtils.hasText(defaultSortBy)) {
            sortBy = defaultSortBy;
            log.debug("Using default sort field: {}", sortBy);
        }

        // Apply defaultSortBy when sortByParam is not provided
        if (!StringUtils.hasText(sortByParam) && StringUtils.hasText(defaultSortBy)) {
            sortByParam = defaultSortBy;
            log.debug("Using default sort field: {}", sortByParam);
        }

        try {
            if (offsetStr != null && !offsetStr.isEmpty()) {
                offset = Integer.parseInt(offsetStr);
                log.debug("Parsed offset parameter: {}", offset);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid offset parameter: '{}'.", offsetStr);
            throw new PageSortValidationException("Invalid offset parameter: " + offsetStr);
        }

        try {
            if (limitStr != null && !limitStr.isEmpty()) {
                limit = Integer.parseInt(limitStr);
                log.debug("Parsed limit parameter: {}", limit);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid limit parameter: '{}'.", limitStr);
            throw new PageSortValidationException("Invalid limit parameter: " + limitStr);
        }

        // Apply validation if the annotation is present
        if (pageSortConfig != null) {
            log.debug("Validating parameters using @PageSortConfig constraints");
            try {
                validateOffset(offset, pageSortConfig.minOffset());
                validateLimit(limit, pageSortConfig.minLimit(), pageSortConfig.maxLimit());
                validateSortBy(sortBy, pageSortConfig.validSortFields());
                validateSortDir(sortDir);
                log.debug("All pagination parameters are valid");
            } catch (PageSortValidationException e) {
                log.error("Validation error: {}", e.getMessage());
                throw e;
            }
        }

        PageSortRequest request = new PageSortRequest(offset, limit, sortBy, sortDir);
        log.info("Created PageSortRequest: offset={}, limit={}, sortBy={}, sortDir={}",
                request.offset(), request.limit(), request.sortBy(), request.sortDir());
        return request;
    }

    /**
     * Validates that the offset is not less than the minimum allowed value.
     *

     * @param offset    the offset to validate
     * @param minOffset the minimum allowed offset
     * @throws PageSortValidationException if the offset is less than the minimum

     */
    private void validateOffset(int offset, int minOffset) {
        log.trace("Validating offset={} against minOffset={}", offset, minOffset);
        if (offset < minOffset) {
            log.warn("Invalid offset: {} is less than minimum: {}", offset, minOffset);
            throw new PageSortValidationException("Offset cannot be less than " + minOffset);
        }
    }

    /**
     * Validates that the limit is within the allowed range.
     *
     * @param limit    the limit to validate
     * @param minLimit the minimum allowed limit
     * @param maxLimit the maximum allowed limit
     * @throws PageSortValidationException if the limit is outside the allowed range
     */
    private void validateLimit(int limit, int minLimit, int maxLimit) {
        log.trace("Validating limit={} against minLimit={}, maxLimit={}", limit, minLimit, maxLimit);
        if (limit < minLimit) {
            log.warn("Invalid limit: {} is less than minimum: {}", limit, minLimit);
            throw new PageSortValidationException("Limit cannot be less than " + minLimit);
        }
        if (limit > maxLimit) {
            log.warn("Invalid limit: {} is greater than maximum: {}", limit, maxLimit);
            throw new PageSortValidationException("Limit cannot be greater than " + maxLimit);
        }
    }

    /**
     * Validates that the sort field is allowed.
     * <p>
     * If the validSortFields array is empty, no sorting is allowed.
     * If the validSortFields array is not empty, the sortBy parameter must be one of the allowed values.
     *
     * @param sortBy          the sort field to validate
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
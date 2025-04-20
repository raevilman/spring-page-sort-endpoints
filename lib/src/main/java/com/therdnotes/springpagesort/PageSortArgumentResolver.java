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
 */
@Component
@Slf4j
public class PageSortArgumentResolver implements HandlerMethodArgumentResolver {

    public PageSortArgumentResolver() {
        log.info("PageSortArgumentResolver initialized");
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean supported = parameter.getParameterType().equals(PageSortRequest.class);
        log.debug("PageSortArgumentResolver.supportsParameter: {} for parameter type {}",
                supported, parameter.getParameterType().getSimpleName());
        return supported;
    }

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
            log.warn("Invalid page parameter: '{}'. Using default page: {}", pageParam, defaultPage);
            page = defaultPage;
        }

        try {
            if (sizeParam != null && !sizeParam.isEmpty()) {
                size = Integer.parseInt(sizeParam);
                log.debug("Parsed size parameter: {}", size);
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid size parameter: '{}'. Using default size: {}", sizeParam, defaultSize);
            size = defaultSize;
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

    private void validatePage(int page, int minPage) {
        log.trace("Validating page={} against minPage={}", page, minPage);
        if (page < minPage) {
            log.warn("Invalid page number: {} is less than minimum: {}", page, minPage);
            throw new PageSortValidationException("Page number cannot be less than " + minPage);
        }
    }

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

    private void validateSortDir(String sortDir) {
        log.trace("Validating sortDir={}", sortDir);
        if (sortDir != null && !sortDir.isBlank() &&
                !("asc".equalsIgnoreCase(sortDir) || "desc".equalsIgnoreCase(sortDir))) {
            log.warn("Invalid sort direction: {}", sortDir);
            throw new PageSortValidationException("Invalid sort direction. Valid options are: asc, desc");
        }
        if (sortDir != null && !sortDir.isBlank()) {
            log.debug("Sort direction '{}' is valid", sortDir);
        }
    }
}
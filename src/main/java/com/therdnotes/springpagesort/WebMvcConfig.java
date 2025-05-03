package com.therdnotes.springpagesort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration class that customizes Spring MVC by registering custom argument resolvers.
 * <p>
 * This class is responsible for integrating the {@link PageSortArgumentResolver} with Spring's
 * MVC infrastructure, enabling automatic resolution of {@link PageSortRequest} objects from
 * HTTP request parameters in controller methods.
 * <p>
 * By implementing {@link WebMvcConfigurer} and overriding {@code addArgumentResolvers},
 * this configuration class hooks into Spring's MVC initialization process.
 *
 * @see PageSortArgumentResolver
 * @see PageSortRequest
 * @see WebMvcConfigurer
 */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * The custom argument resolver that handles pagination and sorting parameters.
     * Injected via constructor dependency injection.
     */
    private final PageSortArgumentResolver pageSortArgumentResolver;

    /**
     * Constructs a new WebMvcConfig with the specified PageSortArgumentResolver.
     *
     * @param pageSortArgumentResolver the resolver responsible for creating {@link PageSortRequest}
     *                                objects from HTTP request parameters
     */
    public WebMvcConfig(PageSortArgumentResolver pageSortArgumentResolver) {
        log.info("WebMvcConfig initialized with PageSortArgumentResolver");
        this.pageSortArgumentResolver = pageSortArgumentResolver;
    }

    /**
     * Adds the PageSortArgumentResolver to Spring MVC's list of argument resolvers.
     * <p>
     * This method is called by Spring during application startup to customize the
     * MVC configuration. By adding our custom resolver here, we enable automatic
     * parameter resolution for controller methods that accept {@link PageSortRequest}.
     *
     * @param resolvers the list of argument resolvers to be extended
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        log.info("Adding PageSortArgumentResolver to argument resolvers");
        resolvers.add(pageSortArgumentResolver);
    }
}
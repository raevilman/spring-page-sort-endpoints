package com.therdnotes.springpagesort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvcConfig is a configuration class that implements WebMvcConfigurer to customize the Spring MVC
 * configuration. It adds custom argument resolvers to handle pagination and sorting parameters in
 * controller methods.
 * <br>
 * This class is used to register the PageSortArgumentResolver, which resolves PageSortRequest objects
 * from HTTP request parameters.
 */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    private final PageSortArgumentResolver pageSortArgumentResolver;

    public WebMvcConfig(PageSortArgumentResolver pageSortArgumentResolver) {
        log.info("WebMvcConfig initialized with PageSortArgumentResolver");
        this.pageSortArgumentResolver = pageSortArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        log.info("Adding PageSortArgumentResolver to argument resolvers");
        resolvers.add(pageSortArgumentResolver);
    }
}
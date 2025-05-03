package com.therdnotes.springpagesort;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Auto-configuration for Spring pagination and sorting support.
 * <p>
 * This configuration is automatically activated for web applications that use Spring MVC.
 * It registers the necessary components for pagination and sorting functionality:
 * <ul>
 *   <li>PageSortArgumentResolver - Handles conversion of request parameters to PageSortRequest objects</li>
 *   <li>WebMvcConfig - Configures the argument resolver in the Spring MVC context</li>
 *   <li>PageSortExceptionHandler - Provides error handling for pagination validation issues</li>
 * </ul>
 * <p>
 * This auto-configuration is conditionally enabled when:
 * <ul>
 *   <li>The application is a web application</li>
 *   <li>WebMvcConfigurer is on the classpath</li>
 * </ul>
 *
 * @see PageSortArgumentResolver
 * @see WebMvcConfig
 * @see PageSortExceptionHandler
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(WebMvcConfigurer.class)
@Import({WebMvcConfig.class, PageSortExceptionHandler.class})
public class PageSortAutoConfiguration {

    /**
     * Creates and registers the PageSortArgumentResolver bean.
     * <p>
     * This resolver handles the conversion of HTTP request parameters into
     * PageSortRequest objects for controller methods.
     *
     * @return a new instance of PageSortArgumentResolver
     */
    @Bean
    public PageSortArgumentResolver pageSortArgumentResolver() {
        return new PageSortArgumentResolver();
    }
}
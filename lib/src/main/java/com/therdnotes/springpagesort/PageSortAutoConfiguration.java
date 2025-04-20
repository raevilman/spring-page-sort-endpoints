package com.therdnotes.springpagesort;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(WebMvcConfigurer.class)
@Import({WebMvcConfig.class, PageSortExceptionHandler.class})
public class PageSortAutoConfiguration {

    @Bean
    public PageSortArgumentResolver pageSortArgumentResolver() {
        return new PageSortArgumentResolver();
    }
}
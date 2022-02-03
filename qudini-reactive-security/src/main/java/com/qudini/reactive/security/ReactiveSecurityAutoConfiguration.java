package com.qudini.reactive.security;

import com.qudini.reactive.security.web.AccessDeniedExceptionTransformingFilter;
import com.qudini.reactive.security.web.LoggingContextPopulatingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactiveSecurityAutoConfiguration {

    @Bean
    public LoggingContextPopulatingFilter loggingContextPopulatingWebFilter() {
        return new LoggingContextPopulatingFilter();
    }

    @Bean
    public AccessDeniedExceptionTransformingFilter accessDeniedExceptionTransformingWebFilter() {
        return new AccessDeniedExceptionTransformingFilter();
    }

}

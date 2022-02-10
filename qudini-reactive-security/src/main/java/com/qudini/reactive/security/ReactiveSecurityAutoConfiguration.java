package com.qudini.reactive.security;

import com.qudini.reactive.security.web.AccessDeniedExceptionHandlingFilter;
import com.qudini.reactive.security.web.AuthenticatingFilter;
import com.qudini.reactive.security.web.AuthenticationService;
import com.qudini.reactive.security.web.CsrfVerifier;
import com.qudini.reactive.security.web.LoggingContextPopulatingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Collection;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;
import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHORIZATION;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ReactiveSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity serverHttpSecurity,
            Collection<AuthenticationService<?>> authenticationServices
    ) {
        return serverHttpSecurity
                .csrf().disable()
                .addFilterAt(new AuthenticatingFilter(authenticationServices), AUTHENTICATION)
                .addFilterBefore(new AccessDeniedExceptionHandlingFilter(), AUTHORIZATION)
                .build();
    }

    @Bean
    public CsrfVerifier csrfVerifier(
            @Value("${csrf.header-name:X-Xsrf-Token}") String headerName,
            @Value("${csrf.cookie-name:XSRF-TOKEN}") String cookieName
    ) {
        return new CsrfVerifier(headerName, cookieName);
    }

    @Bean
    public LoggingContextPopulatingFilter loggingContextPopulatingFilter() {
        return new LoggingContextPopulatingFilter();
    }

    @Bean
    public AccessDeniedExceptionHandlingFilter accessDeniedExceptionHandlingFilter() {
        return new AccessDeniedExceptionHandlingFilter();
    }

}
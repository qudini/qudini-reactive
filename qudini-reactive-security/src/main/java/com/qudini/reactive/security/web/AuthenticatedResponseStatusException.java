package com.qudini.reactive.security.web;

import com.qudini.reactive.logging.WithLoggingContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static com.qudini.reactive.security.web.LoggingContextPopulatingFilter.PRINCIPAL_KEY;

public final class AuthenticatedResponseStatusException extends ResponseStatusException implements WithLoggingContext {

    private static final long serialVersionUID = 1L;

    private final String principal;

    public AuthenticatedResponseStatusException(String principal, HttpStatus status) {
        super(status);
        this.principal = principal;
    }

    public AuthenticatedResponseStatusException(String principal, HttpStatus status, String reason) {
        super(status, reason);
        this.principal = principal;
    }

    public AuthenticatedResponseStatusException(String principal, HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
        this.principal = principal;
    }

    public AuthenticatedResponseStatusException(String principal, int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
        this.principal = principal;
    }

    @Override
    public Map<String, String> getLoggingContext() {
        return Map.of(PRINCIPAL_KEY, principal);
    }

}

package com.qudini.reactive.security.web.csrf;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public final class CsrfTokensNotEqualException extends ResponseStatusException {

    private static final long serialVersionUID = 1L;

    public CsrfTokensNotEqualException(String headerName, String cookieName) {
        super(UNAUTHORIZED, "Expected header '" + headerName + "' and cookie '" + cookieName + "' to be equal");
    }

}

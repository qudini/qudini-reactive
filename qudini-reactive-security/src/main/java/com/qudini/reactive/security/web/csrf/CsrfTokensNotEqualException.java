package com.qudini.reactive.security.web.csrf;

import org.springframework.web.server.ResponseStatusException;

import java.io.Serial;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public final class CsrfTokensNotEqualException extends ResponseStatusException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CsrfTokensNotEqualException(String headerName, String cookieName) {
        super(UNAUTHORIZED, "Expected header '" + headerName + "' and cookie '" + cookieName + "' to be equal");
    }

}

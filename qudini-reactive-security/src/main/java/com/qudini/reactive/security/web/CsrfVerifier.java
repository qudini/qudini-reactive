package com.qudini.reactive.security.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public final class CsrfVerifier {

    private final String headerName;
    private final String cookieName;

    public boolean verify(ServerWebExchange exchange) {
        var optionalHeaderValue = getHeaderValue(exchange);
        var optionalCookieValue = getCookieValue(exchange);
        if (optionalHeaderValue.isPresent() && optionalHeaderValue.equals(optionalCookieValue)) {
            return true;
        } else {
            log.warn("CSRF verification failed, received header {}={} and cookie {}={}", headerName, optionalHeaderValue, cookieName, optionalCookieValue);
            return false;
        }
    }

    private Optional<String> getHeaderValue(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(headerName));
    }

    private Optional<String> getCookieValue(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getCookies().getFirst(cookieName)).map(HttpCookie::getValue);
    }

}

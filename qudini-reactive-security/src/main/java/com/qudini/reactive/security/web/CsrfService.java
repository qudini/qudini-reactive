package com.qudini.reactive.security.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

@Service
@Slf4j
public class CsrfService {

    // FIXME customisable
    private static final String COOKIE_NAME = "XSRF-TOKEN";
    private static final String HEADER_NAME = "X-Xsrf-Token";

    public boolean verify(ServerWebExchange exchange) {
        var optionalHeaderValue = getHeaderValue(exchange);
        var optionalCookieValue = getCookieValue(exchange);
        if (optionalHeaderValue.isPresent() && optionalHeaderValue.equals(optionalCookieValue)) {
            return true;
        } else {
            log.warn("CSRF verification failed, received header {}={} and cookie {}={}", HEADER_NAME, optionalHeaderValue, COOKIE_NAME, optionalCookieValue);
            return false;
        }
    }

    private Optional<String> getHeaderValue(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(HEADER_NAME));
    }

    private Optional<String> getCookieValue(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getCookies().getFirst(COOKIE_NAME)).map(HttpCookie::getValue);
    }

}

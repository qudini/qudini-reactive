package com.qudini.reactive.security.web.csrf;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

@Slf4j
@Value
public class CsrfVerifier {

    String headerName;
    String cookieName;

    /**
     * <p>Throws {@link CsrfValuesNotFoundException} if either the header or the cookie is not found.</p>
     * <p>Throws {@link CsrfValuesNotEqualException} if the header and the cookie are found but not equal.</p>
     */
    public void verify(ServerWebExchange exchange) {
        var optionalHeaderValue = getHeaderValue(exchange);
        var optionalCookieValue = getCookieValue(exchange);
        if (optionalHeaderValue.isEmpty() || optionalCookieValue.isEmpty()) {
            throw new CsrfValuesNotFoundException(headerName, cookieName);
        } else if (!optionalHeaderValue.equals(optionalCookieValue)) {
            throw new CsrfValuesNotEqualException(headerName, cookieName);
        }
    }

    private Optional<String> getHeaderValue(ServerWebExchange exchange) {
        return Optional
                .ofNullable(exchange.getRequest().getHeaders().getFirst(headerName))
                .filter(value -> !value.isBlank());
    }

    private Optional<String> getCookieValue(ServerWebExchange exchange) {
        return Optional
                .ofNullable(exchange.getRequest().getCookies().getFirst(cookieName))
                .map(HttpCookie::getValue)
                .filter(value -> !value.isBlank());
    }

}

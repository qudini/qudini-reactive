package com.qudini.reactive.utils;

import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Management {

    /**
     * Returns true if the request is handled by the management server.
     */
    public static boolean isManagementServer(ServerRequest request) {
        return isManagementServer(request.exchange());
    }

    /**
     * Returns true if the exchange is handled by the management server.
     */
    public static boolean isManagementServer(ServerWebExchange exchange) {
        return Optional
                .ofNullable(exchange.getApplicationContext())
                .filter(Management::isManagement)
                .isPresent();
    }

    /**
     * Returns true if the context is the management application context.
     * See org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest.AbstractWebExchangeMatcher#ignoreApplicationContext(org.springframework.context.ApplicationContext).
     */
    public static boolean isManagement(ApplicationContext context) {
        return context.getParent() != null
                && context.getParent().getId() != null
                && context.getParent().getId().concat(":management").equals(context.getId());
    }

}

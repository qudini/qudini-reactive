package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.Log;
import com.qudini.reactive.logging.WithLoggingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

import static com.qudini.reactive.logging.Log.withLoggingContext;

@Slf4j
public final class ExceptionHandlingFilter extends DefaultErrorWebExceptionHandler implements WebFilter, Ordered {

    // before org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration.errorWebExceptionHandler,
    // after org.springframework.security.config.annotation.web.reactive.WebFluxSecurityConfiguration.springSecurityWebFilterChainFilter:
    public static final int ORDER = -50;

    public ExceptionHandlingFilter(
            ErrorAttributes errorAttributes,
            WebProperties.Resources resources,
            ErrorProperties errorProperties,
            ApplicationContext applicationContext
    ) {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain
                .filter(exchange)
                .onErrorResume(e -> handle(exchange, e));
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        var loggingContext = Optional.of(throwable)
                .filter(WithLoggingContext.class::isInstance)
                .map(WithLoggingContext.class::cast)
                .map(WithLoggingContext::getLoggingContext)
                .orElseGet(Map::of);
        return super
                .handle(exchange, throwable)
                .doOnEach(Log.onComplete(() -> log(exchange, throwable)))
                .contextWrite(withLoggingContext(loggingContext));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    protected void logError(ServerRequest request, ServerResponse response, Throwable throwable) {
        // reimplemented in #log
    }

    private void log(ServerWebExchange exchange, Throwable throwable) {
        var request = exchange.getRequest();
        Optional
                .ofNullable(exchange.getResponse().getStatusCode())
                .filter(HttpStatusCode::isError)
                .ifPresent(status -> {
                    var method = request.getMethod();
                    var path = request.getPath().pathWithinApplication().value();
                    if (status.is4xxClientError()) {
                        log.warn("'{} {}' returned {}", method, path, status, throwable);
                    } else {
                        log.error("'{} {}' returned {}", method, path, status, throwable);
                    }
                });
    }

}

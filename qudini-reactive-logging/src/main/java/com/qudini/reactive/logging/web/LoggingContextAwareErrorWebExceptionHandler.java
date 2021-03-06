package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
public final class LoggingContextAwareErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    public LoggingContextAwareErrorWebExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties.Resources resources,
            ErrorProperties errorProperties,
            ApplicationContext applicationContext
    ) {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        return super.handle(exchange, throwable).doOnEach(Log.onComplete(() -> log(exchange, throwable)));
    }

    @Override
    protected void logError(ServerRequest request, ServerResponse response, Throwable throwable) {
        // reimplemented in #log
    }

    private void log(ServerWebExchange exchange, Throwable throwable) {
        var request = exchange.getRequest();
        Optional
                .ofNullable(exchange.getResponse().getStatusCode())
                .filter(HttpStatus::is5xxServerError)
                .ifPresent(status -> log.error("{} {} returned {}", request.getMethod(), request.getPath(), status, throwable));
    }

}

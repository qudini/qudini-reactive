package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
public final class LoggingContextAwareErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    public LoggingContextAwareErrorWebExceptionHandler(
            ErrorAttributes errorAttributes,
            ResourceProperties resourceProperties,
            ErrorProperties errorProperties,
            ApplicationContext applicationContext
    ) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        return super.handle(exchange, throwable).doOnEach(Log.onComplete(() -> log(exchange, throwable)));
    }

    @Override
    protected void logError(ServerRequest request, ServerResponse response, Throwable throwable) {
        // reimplemented in #log
    }

    private Mono<Void> log(ServerWebExchange exchange, Throwable throwable) {
        var request = exchange.getRequest();
        Optional
                .ofNullable(exchange.getResponse().getStatusCode())
                .ifPresentOrElse(
                        status -> {
                            log.error("An error made {} {} return {}", request.getMethod(), request.getPath(), status, throwable);
//                            switch (status.series()) {
//                                case INFORMATIONAL:
//                                case SUCCESSFUL:
//                                case REDIRECTION:
//                                case CLIENT_ERROR:
//                                    if (log.isDebugEnabled()) {
//                                        log.debug("", throwable);
//                                    }
//                                    break;
//                                case SERVER_ERROR:
//                                    log.error("", throwable);
//                                    break;
//                            }
                        },
                        () -> {
                            log.error("An error made {} {} return an unknown status", request.getMethod(), request.getPath(), throwable);
                        }
                );
        return Mono.empty();
    }

}

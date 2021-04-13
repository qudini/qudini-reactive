package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.Log;
import com.qudini.reactive.logging.ReactiveLoggingContextCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

import static com.qudini.reactive.utils.MoreTuples.onBoth;

@Slf4j
@RequiredArgsConstructor
public final class LoggingContextHttpHandlerDecorator implements HttpHandler {

    private final HttpHandler delegate;

    private final String correlationIdHeader;

    private final LoggingContextExtractor loggingContextExtractor;

    private final ReactiveLoggingContextCreator reactiveLoggingContextCreator;

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        return Mono
                .zip(
                        Mono.just(extractCorrelationId(request)),
                        extractLoggingContext(request)
                )
                .map(onBoth(reactiveLoggingContextCreator::create))
                .flatMap(context -> delegate.handle(request, response).contextWrite(context));
    }

    private Optional<String> extractCorrelationId(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst(correlationIdHeader));
    }

    private Mono<Map<String, String>> extractLoggingContext(ServerHttpRequest request) {
        return loggingContextExtractor
                .extract(request)
                .doOnEach(Log.onError(e -> log.error("An error occurred while extracting logging context", e)))
                .onErrorResume(e -> DefaultLoggingContextExtractor.INSTANCE.extract(request));
    }

}

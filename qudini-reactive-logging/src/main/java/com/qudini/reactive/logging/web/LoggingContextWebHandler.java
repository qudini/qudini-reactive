package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.Log;
import com.qudini.reactive.logging.ReactiveLoggingContextCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.reactive.ContextPathCompositeHandler;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

import static com.qudini.reactive.utils.MoreTuples.onBoth;

@Slf4j
@RequiredArgsConstructor
public final class LoggingContextWebHandler implements WebHandler {

    private final WebHandler delegate;

    private final String correlationIdHeader;

    private final LoggingContextExtractor loggingContextExtractor;

    private final ReactiveLoggingContextCreator reactiveLoggingContextCreator;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return Mono
                .zip(
                        Mono.just(extractCorrelationId(exchange)),
                        extractLoggingContext(exchange)
                )
                .map(onBoth(reactiveLoggingContextCreator::create))
                .flatMap(context -> delegate.handle(exchange).subscriberContext(context));
    }

    private Optional<String> extractCorrelationId(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(correlationIdHeader));
    }

    private Mono<Map<String, String>> extractLoggingContext(ServerWebExchange exchange) {
        return loggingContextExtractor
                .extract(exchange)
                .doOnEach(Log.onError(e -> log.error("An error occurred while extracting logging context", e)))
                .onErrorResume(e -> DefaultLoggingContextExtractor.INSTANCE.extract(exchange));
    }

    public static HttpHandler createHttpHandler(
            ObjectProvider<WebFluxProperties> webFluxProperties,
            ApplicationContext applicationContext,
            String correlationIdHeader,
            LoggingContextExtractor loggingContextExtractor,
            ReactiveLoggingContextCreator reactiveLoggingContextCreator
    ) {

        var originalHttpHandler = (HttpWebHandlerAdapter) WebHttpHandlerBuilder.applicationContext(applicationContext).build();

        var loggingContextAwareWebHandler = new LoggingContextWebHandler(originalHttpHandler.getDelegate(), correlationIdHeader, loggingContextExtractor, reactiveLoggingContextCreator);

        // see org.springframework.web.server.adapter.WebHttpHandlerBuilder.build:
        var loggingContextAwareHttpHandler = new HttpWebHandlerAdapter(loggingContextAwareWebHandler);
        loggingContextAwareHttpHandler.setSessionManager(originalHttpHandler.getSessionManager());
        loggingContextAwareHttpHandler.setCodecConfigurer(originalHttpHandler.getCodecConfigurer());
        loggingContextAwareHttpHandler.setLocaleContextResolver(originalHttpHandler.getLocaleContextResolver());
        Optional.ofNullable(originalHttpHandler.getForwardedHeaderTransformer()).ifPresent(loggingContextAwareHttpHandler::setForwardedHeaderTransformer);
        Optional.ofNullable(originalHttpHandler.getApplicationContext()).ifPresent(loggingContextAwareHttpHandler::setApplicationContext);

        // see org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration.AnnotationConfig.httpHandler
        return Optional
                .ofNullable(webFluxProperties.getIfAvailable())
                .map(WebFluxProperties::getBasePath)
                .filter(StringUtils::hasText)
                .map(basePath -> Map.of(basePath, loggingContextAwareHttpHandler))
                .<HttpHandler>map(ContextPathCompositeHandler::new)
                .orElse(loggingContextAwareHttpHandler);

    }

}

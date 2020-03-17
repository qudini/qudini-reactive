package com.qudini.reactive.logging.web;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public interface CorrelationIdForwarder {

    /**
     * <p>Forwards the correlation id on the given request if any.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * var request = WebClient.create().post();
     * return correlationIdForwarder
     *     .forwardOn(request)
     *     .flatMap(WebClient.RequestHeadersSpec::exchange);
     * }</pre>
     */
    Mono<WebClient.RequestBodySpec> forwardOn(WebClient.RequestBodySpec webClient);

    /**
     * <p>Forwards the correlation id onto the given request if any.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * var request = WebClient.create().get();
     * return correlationIdForwarder
     *     .forwardOn(request)
     *     .flatMap(WebClient.RequestHeadersSpec::exchange);
     * }</pre>
     */
    Mono<WebClient.RequestHeadersSpec<?>> forwardOn(WebClient.RequestHeadersSpec<?> webClient);

}

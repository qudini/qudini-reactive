package com.qudini.reactive.logging;

import reactor.util.context.ContextView;

import java.util.Map;
import java.util.Optional;

public interface ReactiveLoggingContextCreator {

    /**
     * <p>Prepares a reactive context with a correlation id and a logging context.</p>
     */
    ContextView create(Optional<String> correlationId, Map<String, String> loggingContext);

    /**
     * <p>Prepares a reactive context with a correlation id.</p>
     */
    default ContextView create(Optional<String> correlationId) {
        return create(correlationId, Map.of());
    }

    /**
     * <p>Prepares a reactive context with a logging context.</p>
     */
    default ContextView create(Map<String, String> loggingContext) {
        return create(Optional.empty(), loggingContext);
    }

    /**
     * <p>Prepares a reactive context.</p>
     */
    default ContextView create() {
        return create(Optional.empty(), Map.of());
    }

}

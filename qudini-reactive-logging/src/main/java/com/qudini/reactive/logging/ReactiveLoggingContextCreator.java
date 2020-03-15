package com.qudini.reactive.logging;

import reactor.util.context.Context;

import java.util.Map;
import java.util.Optional;

public interface ReactiveLoggingContextCreator {

    /**
     * <p>Prepares a reactive context with a correlation id and a logging context.</p>
     */
    Context create(Optional<String> correlationId, Map<String, String> loggingContext);

}

package com.qudini.reactive.logging;

import reactor.util.context.Context;

import java.util.Map;
import java.util.Optional;

public interface ReactiveLoggingContextCreator {

    /**
     * Prepares a reactive context with a correlation id and a logging context.
     */
    Context create(Optional<String> correlationId, Map<String, String> loggingContext);

}

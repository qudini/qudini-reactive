package com.qudini.reactive.logging;

import reactor.util.context.Context;

import java.util.Map;
import java.util.Optional;

public interface ReactiveContextCreator {

    Context create(Optional<String> correlationId, Map<String, String> loggingContext);

}

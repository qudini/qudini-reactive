package com.qudini.reactive.logging.correlation;

public interface CorrelationIdGenerator {

    /**
     * Generates a new correlation id.
     */
    String generate();

}

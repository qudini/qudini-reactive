package com.qudini.reactive.logging.correlation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DefaultCorrelationIdGenerator")
class DefaultCorrelationIdGeneratorTest {

    @Test
    @DisplayName("should generate a correlation id")
    void generate() {
        var generator = new DefaultCorrelationIdGenerator("Test=");
        var correlationId = generator.generate();
        assertThat(correlationId).matches("Test=1-[a-z0-9]+-[a-z0-9]+");
    }

}

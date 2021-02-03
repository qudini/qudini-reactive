package com.qudini.reactive.logging.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.reactive.ServerHttpRequest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultLoggingContextExtractor")
class DefaultLoggingContextExtractorTest {

    @Mock
    private ServerHttpRequest request;

    @InjectMocks
    private DefaultLoggingContextExtractor extractor;

    @Test
    @DisplayName("should return an empty map")
    void emptyMap() {
        var context = extractor.extract(request).block();
        assertThat(context).isEmpty();
    }

}

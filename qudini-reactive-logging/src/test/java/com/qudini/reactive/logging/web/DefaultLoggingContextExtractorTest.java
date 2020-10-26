package com.qudini.reactive.logging.web;

import com.qudini.reactive.utils.build.BuildInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultLoggingContextExtractor")
class DefaultLoggingContextExtractorTest {

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private BuildInfoService buildInfoService;

    @InjectMocks
    private DefaultLoggingContextExtractor extractor;

    @Test
    @DisplayName("should return a map holding the build version")
    void emptyMap() {
        given(buildInfoService.getVersion()).willReturn("42");
        var context = extractor.extract(exchange).block();
        assertThat(context).isEqualTo(Map.of("build_version", "42"));
    }

}

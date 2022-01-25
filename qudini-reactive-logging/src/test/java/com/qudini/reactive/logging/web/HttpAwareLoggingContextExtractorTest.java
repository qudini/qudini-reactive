package com.qudini.reactive.logging.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("HttpAwareLoggingContextExtractor")
class HttpAwareLoggingContextExtractorTest {

    @Mock
    private ServerHttpRequest request;

    @InjectMocks
    private HttpAwareLoggingContextExtractor extractor;

    @Test
    @DisplayName("should extract metadata from the request")
    void requestMetadata() {
        var headers = new HttpHeaders();
        headers.addAll("User-Agent", List.of("user", "agent"));
        given(request.getMethodValue()).willReturn("POST");
        given(request.getPath()).willReturn(RequestPath.parse("/context/some/path", "/context"));
        given(request.getHeaders()).willReturn(headers);
        var context = extractor.extract(request).block();
        assertThat(context).containsExactlyInAnyOrderEntriesOf(Map.of(
                "request", "POST /some/path",
                "user_agent", "user, agent"
        ));
    }

}

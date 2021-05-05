package com.qudini.reactive.metrics.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;
import static org.springframework.http.HttpMethod.POST;

@DisplayName("ProbesMatcher")
class ProbesMatcherTest {

    private static final int SERVER_PORT = 1;
    private static final int MANAGEMENT_SERVER_PORT = 2;

    @Test
    @DisplayName("should allow valid requests with default paths")
    void testValidDefaultPaths() {
        var matcher = new ProbesMatcher(SERVER_PORT, MANAGEMENT_SERVER_PORT);
        assertThat(process(matcher, mockExchange(HEAD, "/liveness", SERVER_PORT))).isNotNull();
        assertThat(process(matcher, mockExchange(GET, "/liveness", SERVER_PORT))).isNotNull();
        assertThat(process(matcher, mockExchange(HEAD, "/liveness", MANAGEMENT_SERVER_PORT))).isNotNull();
        assertThat(process(matcher, mockExchange(GET, "/liveness", MANAGEMENT_SERVER_PORT))).isNotNull();
        assertThat(process(matcher, mockExchange(GET, "/readiness", MANAGEMENT_SERVER_PORT))).isNotNull();
        assertThat(process(matcher, mockExchange(GET, "/metrics", MANAGEMENT_SERVER_PORT))).isNotNull();
    }

    @Test
    @DisplayName("should forbid invalid requests with default paths")
    void testInvalidDefaultPaths() {
        var matcher = new ProbesMatcher(SERVER_PORT, MANAGEMENT_SERVER_PORT);
        assertThat(process(matcher, mockExchange(POST, "/metrics", MANAGEMENT_SERVER_PORT))).isNull();
        assertThat(process(matcher, mockExchange(GET, "/foobar", MANAGEMENT_SERVER_PORT))).isNull();
        assertThat(process(matcher, mockExchange(GET, "/metrics", SERVER_PORT))).isNull();
    }

    @Test
    @DisplayName("should allow valid requests with custom paths")
    void testValidCustomPaths() {
        var matcher = new ProbesMatcher(SERVER_PORT, MANAGEMENT_SERVER_PORT, Paths.builder().liveness("/l").readiness("/r").metrics("/m").build());
        assertThat(process(matcher, mockExchange(HEAD, "/l", SERVER_PORT))).isNotNull();
        assertThat(process(matcher, mockExchange(GET, "/l", SERVER_PORT))).isNotNull();
        assertThat(process(matcher, mockExchange(HEAD, "/l", MANAGEMENT_SERVER_PORT))).isNotNull();
        assertThat(process(matcher, mockExchange(GET, "/l", MANAGEMENT_SERVER_PORT))).isNotNull();
        assertThat(process(matcher, mockExchange(GET, "/r", MANAGEMENT_SERVER_PORT))).isNotNull();
        assertThat(process(matcher, mockExchange(GET, "/m", MANAGEMENT_SERVER_PORT))).isNotNull();
    }

    @Test
    @DisplayName("should allow invalid requests with default paths")
    void testInvalidCustomPaths() {
        var matcher = new ProbesMatcher(SERVER_PORT, MANAGEMENT_SERVER_PORT, Paths.builder().liveness("/l").readiness("/r").metrics("/m").build());
        assertThat(process(matcher, mockExchange(POST, "/m", MANAGEMENT_SERVER_PORT))).isNull();
        assertThat(process(matcher, mockExchange(GET, "/foobar", MANAGEMENT_SERVER_PORT))).isNull();
        assertThat(process(matcher, mockExchange(GET, "/m", SERVER_PORT))).isNull();
    }

    private ServerWebExchange mockExchange(HttpMethod method, String path, int port) {
        var pathWithinApplication = mock(PathContainer.class);
        given(pathWithinApplication.value()).willReturn(path);
        var requestPath = mock(RequestPath.class);
        given(requestPath.pathWithinApplication()).willReturn(pathWithinApplication);
        var request = mock(ServerHttpRequest.class);
        given(request.getMethod()).willReturn(method);
        given(request.getURI()).willReturn(URI.create("http://test:" + port));
        given(request.getPath()).willReturn(requestPath);
        var exchange = mock(ServerWebExchange.class);
        given(exchange.getRequest()).willReturn(request);
        return exchange;
    }

    private ServerWebExchangeMatcher.MatchResult process(ProbesMatcher matcher, ServerWebExchange exchange) {
        return matcher
                .matches(exchange)
                .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                .block();
    }

}

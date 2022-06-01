package com.qudini.reactive.metrics.security;

import com.qudini.reactive.metrics.security.ProbesMatcher.ServerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;

import static com.qudini.reactive.metrics.security.ProbesMatcher.ServerType.APPLICATION;
import static com.qudini.reactive.metrics.security.ProbesMatcher.ServerType.MANAGEMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;
import static org.springframework.http.HttpMethod.POST;

@DisplayName("ProbesMatcher")
class ProbesMatcherTest {

    @Test
    @DisplayName("should allow valid requests with default paths")
    void testValidDefaultPaths() {
        var matcher = ProbesMatcher.probes();
        assertThat(process(matcher, mockExchange(APPLICATION, HEAD, "/liveness"))).isNotNull();
        assertThat(process(matcher, mockExchange(APPLICATION, GET, "/liveness"))).isNotNull();
        assertThat(process(matcher, mockExchange(MANAGEMENT, GET, "/readiness"))).isNotNull();
        assertThat(process(matcher, mockExchange(MANAGEMENT, GET, "/metrics"))).isNotNull();
    }

    @Test
    @DisplayName("should forbid invalid requests with default paths")
    void testInvalidDefaultPaths() {
        var matcher = ProbesMatcher.probes();
        assertThat(process(matcher, mockExchange(MANAGEMENT, POST, "/metrics"))).isNull();
        assertThat(process(matcher, mockExchange(MANAGEMENT, GET, "/foobar"))).isNull();
        assertThat(process(matcher, mockExchange(APPLICATION, GET, "/metrics"))).isNull();
    }

    @Test
    @DisplayName("should allow valid requests with custom paths")
    void testValidCustomPaths() {
        var matcher = ProbesMatcher.probes(ProbesPaths.builder().liveness("/l").readiness("/r").metrics("/m").build());
        assertThat(process(matcher, mockExchange(APPLICATION, HEAD, "/l"))).isNotNull();
        assertThat(process(matcher, mockExchange(APPLICATION, GET, "/l"))).isNotNull();
        assertThat(process(matcher, mockExchange(MANAGEMENT, GET, "/r"))).isNotNull();
        assertThat(process(matcher, mockExchange(MANAGEMENT, GET, "/m"))).isNotNull();
    }

    @Test
    @DisplayName("should allow invalid requests with default paths")
    void testInvalidCustomPaths() {
        var matcher = ProbesMatcher.probes(ProbesPaths.builder().liveness("/l").readiness("/r").metrics("/m").build());
        assertThat(process(matcher, mockExchange(MANAGEMENT, POST, "/m"))).isNull();
        assertThat(process(matcher, mockExchange(MANAGEMENT, GET, "/foobar"))).isNull();
        assertThat(process(matcher, mockExchange(APPLICATION, GET, "/m"))).isNull();
    }

    private ServerWebExchange mockExchange(ServerType serverType, HttpMethod method, String path) {
        var applicationContext = mock(ApplicationContext.class);
        if (serverType == MANAGEMENT) {
            var parentApplicationContext = mock(ApplicationContext.class);
            given(applicationContext.getParent()).willReturn(parentApplicationContext);
            given(parentApplicationContext.getId()).willReturn("ap");
            given(applicationContext.getId()).willReturn("ap:management");
        }
        var pathWithinApplication = mock(PathContainer.class);
        given(pathWithinApplication.value()).willReturn(path);
        var requestPath = mock(RequestPath.class);
        given(requestPath.pathWithinApplication()).willReturn(pathWithinApplication);
        var request = mock(ServerHttpRequest.class);
        given(request.getMethod()).willReturn(method);
        given(request.getPath()).willReturn(requestPath);
        var exchange = mock(ServerWebExchange.class);
        given(exchange.getRequest()).willReturn(request);
        given(exchange.getApplicationContext()).willReturn(applicationContext);
        return exchange;
    }

    private ServerWebExchangeMatcher.MatchResult process(ProbesMatcher matcher, ServerWebExchange exchange) {
        return matcher
                .matches(exchange)
                .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                .block();
    }

}

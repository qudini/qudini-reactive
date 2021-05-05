package com.qudini.reactive.metrics.security;

import lombok.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;

public final class ProbesMatcher implements ServerWebExchangeMatcher {

    private final Set<Request> allowedRequests;

    public ProbesMatcher(int serverPort, int managementServerPort) {
        this(serverPort, managementServerPort, Paths.builder().build());
    }

    public ProbesMatcher(int serverPort, int managementServerPort, Paths paths) {
        this.allowedRequests = Set.of(
                new Request(HEAD, paths.getLiveness(), serverPort),
                new Request(GET, paths.getLiveness(), serverPort),
                new Request(HEAD, paths.getLiveness(), managementServerPort),
                new Request(GET, paths.getLiveness(), managementServerPort),
                new Request(GET, paths.getReadiness(), managementServerPort),
                new Request(GET, paths.getMetrics(), managementServerPort)
        );
    }

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return allowedRequests.contains(Request.fromExchange(exchange))
                ? MatchResult.match()
                : MatchResult.notMatch();
    }

    @Value
    private static class Request {

        HttpMethod method;
        String path;
        int port;

        private static Request fromExchange(ServerWebExchange exchange) {
            var request = exchange.getRequest();
            return new Request(
                    request.getMethod(),
                    request.getPath().pathWithinApplication().value(),
                    request.getURI().getPort()
            );
        }

    }


}

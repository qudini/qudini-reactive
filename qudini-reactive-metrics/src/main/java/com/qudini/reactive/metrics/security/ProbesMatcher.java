package com.qudini.reactive.metrics.security;

import com.qudini.reactive.utils.Management;
import lombok.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.qudini.reactive.metrics.security.ProbesMatcher.ServerType.APPLICATION;
import static com.qudini.reactive.metrics.security.ProbesMatcher.ServerType.MANAGEMENT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;

public final class ProbesMatcher implements ServerWebExchangeMatcher {

    private final Set<Request> allowedRequests;

    private ProbesMatcher(ProbesPaths probesPaths) {
        this.allowedRequests = Set.of(
                new Request(APPLICATION, HEAD, probesPaths.getLiveness()),
                new Request(APPLICATION, GET, probesPaths.getLiveness()),
                new Request(MANAGEMENT, HEAD, probesPaths.getLiveness()),
                new Request(MANAGEMENT, GET, probesPaths.getLiveness()),
                new Request(MANAGEMENT, GET, probesPaths.getReadiness()),
                new Request(MANAGEMENT, GET, probesPaths.getMetrics())
        );
    }

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return allowedRequests.contains(Request.fromExchange(exchange))
                ? MatchResult.match()
                : MatchResult.notMatch();
    }

    enum ServerType {
        APPLICATION,
        MANAGEMENT
    }

    @Value
    private static class Request {

        ServerType type;
        HttpMethod method;
        String path;

        private static Request fromExchange(ServerWebExchange exchange) {
            var request = exchange.getRequest();
            return new Request(
                    Management.isManagementServer(exchange) ? MANAGEMENT : APPLICATION,
                    request.getMethod(),
                    request.getPath().pathWithinApplication().value()
            );
        }

    }

    public static ProbesMatcher probes() {
        return new ProbesMatcher(ProbesPaths.builder().build());
    }

    public static ProbesMatcher probes(ProbesPaths paths) {
        return new ProbesMatcher(paths);
    }

}

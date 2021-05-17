package com.qudini.reactive.metrics.security;

import lombok.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;

public final class ProbesMatcher implements ServerWebExchangeMatcher {

    private final Set<Request> allowedRequests;

    public ProbesMatcher(int managementServerPort) {
        this(managementServerPort, Paths.builder().build());
    }

    public ProbesMatcher(int managementServerPort, Paths paths) {
        this.allowedRequests = Set.of(
                new Request(HEAD, paths.getLiveness(), Optional.empty()),
                new Request(GET, paths.getLiveness(), Optional.empty()),
                new Request(GET, paths.getReadiness(), Optional.of(managementServerPort)),
                new Request(GET, paths.getMetrics(), Optional.of(managementServerPort))
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
        Optional<Integer> port;

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (!(object instanceof Request)) {
                return false;
            }
            var other = (Request) object;
            return getMethod().equals(other.getMethod())
                    && getPath().equals(other.getPath())
                    && (getPort().isEmpty() || other.getPort().isEmpty() || getPort().equals(other.getPort()));
        }

        @Override
        public int hashCode() {
            return Objects.hash(getMethod(), getPath());
        }

        private static Request fromExchange(ServerWebExchange exchange) {
            var request = exchange.getRequest();
            return new Request(
                    request.getMethod(),
                    request.getPath().pathWithinApplication().value(),
                    Optional.of(request.getURI().getPort())
            );
        }

    }

}

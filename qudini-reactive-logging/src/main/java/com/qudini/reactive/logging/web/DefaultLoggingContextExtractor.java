package com.qudini.reactive.logging.web;

import com.qudini.reactive.utils.build.BuildInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public final class DefaultLoggingContextExtractor implements LoggingContextExtractor {

    public static final String BUILD_NAME_KEY = "build_name";
    public static final String BUILD_VERSION_KEY = "build_version";

    private final BuildInfoService buildInfoService;

    @Override
    public Mono<Map<String, String>> extract(ServerWebExchange exchange) {
        return Mono.just(Map.of(
                BUILD_NAME_KEY, buildInfoService.getName(),
                BUILD_VERSION_KEY, buildInfoService.getVersion()
        ));
    }

}

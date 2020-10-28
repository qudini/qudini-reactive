package com.qudini.reactive.metrics;

import com.qudini.reactive.metrics.aop.MeasuredAspect;
import com.qudini.reactive.metrics.build.BuildInfoMeterBinder;
import com.qudini.reactive.utils.metadata.MetadataService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;
import static org.springframework.web.reactive.function.BodyInserters.empty;
import static org.springframework.web.reactive.function.server.RequestPredicates.methods;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class ReactiveMetricsAutoConfiguration {

    @Bean
    public BuildInfoMeterBinder buildInfoMeterBinder(
            MetadataService metadataService,
            @Value("${metrics.build-info.gauge-name-prefix:app}") String gaugeNamePrefix
    ) {
        return new BuildInfoMeterBinder(metadataService, gaugeNamePrefix);
    }

    @Bean
    public RouterFunction<ServerResponse> liveness() {
        return route(
                methods(HEAD, GET).and(path("/liveness")),
                request -> ok().body(empty())
        );
    }

    @Bean
    public MeasuredAspect measuredAspect(MeterRegistry registry) {
        return new MeasuredAspect(registry);
    }

}

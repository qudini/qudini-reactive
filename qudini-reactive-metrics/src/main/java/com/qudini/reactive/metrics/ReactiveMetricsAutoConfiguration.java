package com.qudini.reactive.metrics;

import com.qudini.reactive.metrics.aop.MeasuredAspect;
import com.qudini.reactive.metrics.buildinfo.BuildInfoMeterBinder;
import com.qudini.reactive.metrics.buildinfo.BuildInfoService;
import com.qudini.reactive.metrics.buildinfo.DefaultBuildInfoService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.BodyInserters.empty;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class ReactiveMetricsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BuildInfoService buildInfoService(ApplicationContext applicationContext) {
        return new DefaultBuildInfoService(applicationContext);
    }

    @Bean
    public BuildInfoMeterBinder buildInfoMeterBinder(
            BuildInfoService buildInfoService,
            @Value("${metrics.build-info.gauge-name-prefix:app}") String gaugeNamePrefix
    ) {
        return new BuildInfoMeterBinder(buildInfoService, gaugeNamePrefix);
    }

    @Bean
    public RouterFunction<ServerResponse> liveness() {
        return route(
                GET("/liveness"),
                request -> ok().body(empty())
        );
    }

    @Bean
    public MeasuredAspect measuredAspect(MeterRegistry registry) {
        return new MeasuredAspect(registry);
    }

}

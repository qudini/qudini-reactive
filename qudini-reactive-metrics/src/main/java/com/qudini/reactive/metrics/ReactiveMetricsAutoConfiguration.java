package com.qudini.reactive.metrics;

import com.qudini.reactive.metrics.aop.MeasuredAspect;
import com.qudini.reactive.metrics.buildinfo.BuildInfoMeterBinder;
import com.qudini.reactive.metrics.buildinfo.BuildInfoService;
import com.qudini.reactive.metrics.buildinfo.DefaultBuildInfoMeterBinder;
import com.qudini.reactive.metrics.buildinfo.DefaultBuildInfoService;
import com.qudini.reactive.metrics.health.DefaultLivenessEndpoint;
import com.qudini.reactive.metrics.health.LivenessEndpoint;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactiveMetricsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BuildInfoService buildInfoService(ApplicationContext applicationContext) {
        return new DefaultBuildInfoService(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public BuildInfoMeterBinder buildInfoMeterBinder(BuildInfoService buildInfoService) {
        return new DefaultBuildInfoMeterBinder(buildInfoService);
    }

    @Bean
    @ConditionalOnMissingBean
    public LivenessEndpoint livenessEndpoint() {
        return new DefaultLivenessEndpoint();
    }

    @Bean
    public MeasuredAspect measuredAspect(MeterRegistry registry) {
        return new MeasuredAspect(registry);
    }

}

package com.qudini.reactive.metrics;

import com.qudini.reactive.metrics.aop.MeasuredAspect;
import com.qudini.reactive.metrics.buildinfo.BuildInfoMeterBinder;
import com.qudini.reactive.metrics.buildinfo.BuildInfoService;
import com.qudini.reactive.metrics.buildinfo.DefaultBuildInfoService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
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
    public MeterBinder buildInfoMeterBinder(BuildInfoService buildInfoService) {
        return new BuildInfoMeterBinder(buildInfoService);
    }

    @Bean
    public MeasuredAspect measuredAspect(MeterRegistry registry) {
        return new MeasuredAspect(registry);
    }

}

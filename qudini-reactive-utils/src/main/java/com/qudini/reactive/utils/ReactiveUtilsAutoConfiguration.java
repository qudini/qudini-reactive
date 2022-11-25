package com.qudini.reactive.utils;

import com.qudini.reactive.utils.metadata.DefaultMetadataService;
import com.qudini.reactive.utils.metadata.MetadataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@AutoConfiguration
public class ReactiveUtilsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MetadataService metadataService(
            @Value("${K8S_NAMESPACE:unknown}") String environment,
            ApplicationContext applicationContext
    ) {
        return new DefaultMetadataService(environment, applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public Clock clock() {
        return Clock.systemUTC();
    }

}

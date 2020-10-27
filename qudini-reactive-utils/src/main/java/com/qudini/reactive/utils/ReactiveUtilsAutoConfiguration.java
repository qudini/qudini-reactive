package com.qudini.reactive.utils;

import com.qudini.reactive.utils.metadata.MetadataService;
import com.qudini.reactive.utils.metadata.DefaultMetadataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactiveUtilsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MetadataService buildMetadataService(
            @Value("${K8S_NAMESPACE}") String environment,
            ApplicationContext applicationContext
    ) {
        return new DefaultMetadataService(environment, applicationContext);
    }

}

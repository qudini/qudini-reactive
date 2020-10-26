package com.qudini.reactive.utils;

import com.qudini.reactive.utils.build.BuildInfoService;
import com.qudini.reactive.utils.build.DefaultBuildInfoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactiveUtilsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BuildInfoService buildInfoService(ApplicationContext applicationContext) {
        return new DefaultBuildInfoService(applicationContext);
    }

}

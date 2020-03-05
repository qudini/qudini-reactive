package com.qudini.reactive.logging;

import com.qudini.reactive.logging.aop.DefaultJoinPointSerialiser;
import com.qudini.reactive.logging.aop.JoinPointSerialiser;
import com.qudini.reactive.logging.aop.LoggedAspect;
import com.qudini.reactive.logging.correlation.CorrelationIdGenerator;
import com.qudini.reactive.logging.correlation.DefaultCorrelationIdGenerator;
import com.qudini.reactive.logging.web.DefaultLoggingContextExtractor;
import com.qudini.reactive.logging.web.LoggingContextExtractor;
import com.qudini.reactive.logging.web.LoggingContextFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class ReactiveLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CorrelationIdGenerator correlationIdGenerator(
            @Value("${logging.correlation-id.prefix:}") String prefix
    ) {
        return new DefaultCorrelationIdGenerator(prefix);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactiveLoggingContextCreator reactiveContextCreator(CorrelationIdGenerator correlationIdGenerator) {
        return new Log(correlationIdGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    public LoggingContextExtractor loggingContextExtractor() {
        return new DefaultLoggingContextExtractor();
    }

    @Bean
    public WebFilter loggingContextFilter(
            @Value("${logging.correlation-id.header-name:X-Amzn-Trace-Id}") String correlationIdHeader,
            LoggingContextExtractor loggingContextExtractor,
            ReactiveLoggingContextCreator reactiveLoggingContextCreator
    ) {
        return new LoggingContextFilter(correlationIdHeader, loggingContextExtractor, reactiveLoggingContextCreator);
    }

    @Bean
    @ConditionalOnMissingBean
    public JoinPointSerialiser joinPointSerialiser() {
        return new DefaultJoinPointSerialiser();
    }

    @Bean
    public LoggedAspect loggedAspect(JoinPointSerialiser joinPointSerialiser) {
        return new LoggedAspect(joinPointSerialiser);
    }

}

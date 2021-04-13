package com.qudini.reactive.logging;

import com.qudini.reactive.logging.aop.DefaultJoinPointSerialiser;
import com.qudini.reactive.logging.aop.JoinPointSerialiser;
import com.qudini.reactive.logging.aop.LoggedAspect;
import com.qudini.reactive.logging.correlation.CorrelationIdGenerator;
import com.qudini.reactive.logging.correlation.DefaultCorrelationIdGenerator;
import com.qudini.reactive.logging.log4j2.QudiniLogEvent;
import com.qudini.reactive.logging.web.CorrelationIdForwarder;
import com.qudini.reactive.logging.web.DefaultCorrelationIdForwarder;
import com.qudini.reactive.logging.web.DefaultLoggingContextExtractor;
import com.qudini.reactive.logging.web.LoggingContextAwareErrorWebExceptionHandler;
import com.qudini.reactive.logging.web.LoggingContextExtractor;
import com.qudini.reactive.logging.web.LoggingContextHttpHandlerDecorator;
import com.qudini.reactive.utils.metadata.MetadataService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandlerDecoratorFactory;
import org.springframework.web.reactive.result.view.ViewResolver;

import static java.util.stream.Collectors.toUnmodifiableList;

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
    @ConditionalOnMissingBean
    public CorrelationIdForwarder correlationIdForwarder(
            @Value("${logging.correlation-id.header-name:X-Amzn-Trace-Id}") String correlationIdHeader
    ) {
        return new DefaultCorrelationIdForwarder(correlationIdHeader);
    }

    @Bean
    @ConditionalOnMissingBean
    public JoinPointSerialiser joinPointSerialiser() {
        return new DefaultJoinPointSerialiser();
    }

    @Bean
    @Order(-2)
    public ErrorWebExceptionHandler loggingContextAwareErrorWebExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties webProperties,
            ServerProperties serverProperties,
            ApplicationContext applicationContext,
            ObjectProvider<ViewResolver> viewResolvers,
            ServerCodecConfigurer serverCodecConfigurer
    ) {
        var exceptionHandler = new LoggingContextAwareErrorWebExceptionHandler(
                errorAttributes,
                webProperties.getResources(),
                serverProperties.getError(),
                applicationContext
        );
        exceptionHandler.setViewResolvers(viewResolvers.orderedStream().collect(toUnmodifiableList()));
        exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        return exceptionHandler;
    }

    @Bean
    public HttpHandlerDecoratorFactory loggingContextHttpHandlerDecoratorFactory(
            @Value("${logging.correlation-id.header-name:X-Amzn-Trace-Id}") String correlationIdHeader,
            LoggingContextExtractor loggingContextExtractor,
            ReactiveLoggingContextCreator reactiveLoggingContextCreator
    ) {
        return handler -> new LoggingContextHttpHandlerDecorator(handler, correlationIdHeader, loggingContextExtractor, reactiveLoggingContextCreator);
    }

    @Bean
    public LoggedAspect loggedAspect(JoinPointSerialiser joinPointSerialiser) {
        return new LoggedAspect(joinPointSerialiser);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start(ApplicationReadyEvent applicationReadyEvent) {
        var metadataService = applicationReadyEvent
                .getApplicationContext()
                .getBean(MetadataService.class);
        QudiniLogEvent.init(metadataService);
    }

}

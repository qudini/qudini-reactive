package com.qudini.reactive.logging.log4j2;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

public class ReactiveLoggingApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    private static final String LOGGING_CONFIG_PROPERTY = LoggingApplicationListener.CONFIG_PROPERTY;
    private static final String QUDINI_LOG4J2_CONFIG = "classpath:qudini-log4j2.xml";
    private static final String DEFAULT = "default";
    private static final String PROPERTY_SOURCE = "java-logging-property-source";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        var environment = event.getEnvironment();
        var loggingConfig = environment.getProperty(LOGGING_CONFIG_PROPERTY, "");
        if (loggingConfig.isEmpty()) {
            setLoggingConfig(environment, QUDINI_LOG4J2_CONFIG);
        } else if (DEFAULT.equalsIgnoreCase(loggingConfig)) {
            setLoggingConfig(environment, "");
        }
    }

    private void setLoggingConfig(ConfigurableEnvironment environment, String loggingConfig) {
        var propertySource = new MapPropertySource(PROPERTY_SOURCE, Map.of(LOGGING_CONFIG_PROPERTY, loggingConfig));
        environment.getPropertySources().addFirst(propertySource);
    }

    @Override
    public int getOrder() {
        return LoggingApplicationListener.DEFAULT_ORDER - 1;
    }

}

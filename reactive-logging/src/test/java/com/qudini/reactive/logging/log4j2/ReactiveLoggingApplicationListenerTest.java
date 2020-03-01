package com.qudini.reactive.logging.log4j2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReactiveLoggingApplicationListener")
class ReactiveLoggingApplicationListenerTest {

    @Mock
    private ApplicationEnvironmentPreparedEvent event;

    @Mock
    private ConfigurableEnvironment environment;

    @Mock
    private MutablePropertySources propertySources;

    @Captor
    private ArgumentCaptor<MapPropertySource> propertySourceCaptor;

    @InjectMocks
    private ReactiveLoggingApplicationListener listener;

    @Test
    @DisplayName("should default to the Qudini config")
    void defaultsToQudiniConfig() {
        given(event.getEnvironment()).willReturn(environment);
        given(environment.getProperty(LoggingApplicationListener.CONFIG_PROPERTY, "")).willReturn("");
        given(environment.getPropertySources()).willReturn(propertySources);
        listener.onApplicationEvent(event);
        verify(propertySources, times(1)).addFirst(propertySourceCaptor.capture());
        var propertySource = propertySourceCaptor.getValue();
        assertThat(propertySource.getName()).isEqualTo("java-logging-property-source");
        assertThat(propertySource.getProperty(LoggingApplicationListener.CONFIG_PROPERTY)).isEqualTo("classpath:qudini-log4j2.xml");
    }

    @Test
    @DisplayName("should allow using the default config")
    void allowsUsingDefault() {
        given(event.getEnvironment()).willReturn(environment);
        given(environment.getProperty(LoggingApplicationListener.CONFIG_PROPERTY, "")).willReturn("default");
        given(environment.getPropertySources()).willReturn(propertySources);
        listener.onApplicationEvent(event);
        verify(propertySources, times(1)).addFirst(propertySourceCaptor.capture());
        var propertySource = propertySourceCaptor.getValue();
        assertThat(propertySource.getName()).isEqualTo("java-logging-property-source");
        assertThat(propertySource.getProperty(LoggingApplicationListener.CONFIG_PROPERTY)).isEqualTo("");
    }

    @Test
    @DisplayName("should allow using a custom config")
    void allowsCustom() {
        given(event.getEnvironment()).willReturn(environment);
        given(environment.getProperty(LoggingApplicationListener.CONFIG_PROPERTY, "")).willReturn("custom");
        listener.onApplicationEvent(event);
        verify(propertySources, never()).addFirst(any());
    }

    @Test
    @DisplayName("should run before Spring's LoggingApplicationListener")
    void beforeSpring() {
        assertThat(listener.getOrder()).isLessThan(LoggingApplicationListener.DEFAULT_ORDER);
    }

}

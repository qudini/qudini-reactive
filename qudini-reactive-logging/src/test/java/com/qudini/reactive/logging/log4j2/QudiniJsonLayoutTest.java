package com.qudini.reactive.logging.log4j2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qudini.reactive.utils.metadata.DefaultMetadataService;
import lombok.SneakyThrows;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import static java.time.Duration.between;
import static java.time.Instant.from;
import static java.time.Instant.now;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("QudiniJsonLayout")
class QudiniJsonLayoutTest {

    private static final LoggerContext LOGGER_CONTEXT = (LoggerContext) LogManager.getContext();
    private static final ResettableStringWriter LOG_OUTPUT = new ResettableStringWriter();

    private static final String ENVIRONMENT = "test-env";
    private static final String BUILD_NAME = "test-build";
    private static final String BUILD_VERSION = "test-version";

    @BeforeAll
    static void prepareLogger() {
        var config = LOGGER_CONTEXT.getConfiguration();
        var appender = WriterAppender
                .newBuilder()
                .setLayout(QudiniJsonLayout.newInstance())
                .setTarget(LOG_OUTPUT)
                .setName("STRING_APPENDER")
                .build();
        appender.start();
        var appenderRefs = new AppenderRef[]{
                AppenderRef.createAppenderRef("STRING_APPENDER", null, null)
        };
        var loggerConfig = LoggerConfig
                .newBuilder()
                .withAdditivity(false)
                .withLevel(Level.INFO)
                .withLoggerName("STRING_LOGGER")
                .withIncludeLocation("true")
                .withRefs(appenderRefs)
                .withConfig(config)
                .build();
        loggerConfig.addAppender(appender, null, null);
        config.addLogger("TEST_LOGGER", loggerConfig);
        LOGGER_CONTEXT.updateLoggers();
    }

    @BeforeAll
    static void initLogEvent() {
        QudiniLogEvent.init(new DefaultMetadataService(ENVIRONMENT, BUILD_NAME, BUILD_VERSION));
    }

    @AfterAll
    static void closeOutput() throws Exception {
        LOG_OUTPUT.close();
    }

    @AfterAll
    static void resetLogEvent() {
        QudiniLogEvent.reset();
    }

    @AfterEach
    void resetOutput() throws Exception {
        LOG_OUTPUT.reset();
    }

    @Test
    @DisplayName("should serialise the default entries")
    void standard() {
        LOGGER_CONTEXT.getLogger("TEST_LOGGER").info("log message");
        var json = getLogOutput();
        assertThat(json.get("timestamp")).isNotNull();
        assertThat(between(from(ISO_INSTANT.parse(json.get("timestamp"))), now()).get(SECONDS)).isEqualTo(0);
        assertThat(json.get("level")).isEqualTo("INFO");
        assertThat(json.get("thread")).isEqualTo("main");
        assertThat(json.get("logger")).isEqualTo("TEST_LOGGER");
        assertThat(json.get("message")).isEqualTo("log message");
        assertThat(json.get("stacktrace")).isNull();
        assertThat(json.get("build_name")).isEqualTo(BUILD_NAME);
        assertThat(json.get("build_version")).isEqualTo(BUILD_VERSION);
        assertThat(json.get("env")).isEqualTo(ENVIRONMENT);
    }

    @Test
    @DisplayName("should serialise the MDC entries")
    void mdc() {
        MDC.put("foo", "bar");
        LOGGER_CONTEXT.getLogger("TEST_LOGGER").info("log message");
        var json = getLogOutput();
        assertThat(json.get("foo")).isEqualTo("bar");
        assertThat(json.get("stacktrace")).isNull();
    }

    @Test
    @DisplayName("should serialise the stacktrace when an error is logged")
    void erroneous() {
        try {
            Exception cause = new NullPointerException("null pointer exception message");
            throw new IllegalArgumentException("illegal argument exception message", cause);
        } catch (Exception e) {
            LOGGER_CONTEXT.getLogger("TEST_LOGGER").error("log message", e);
        }
        var json = getLogOutput();
        assertThat(json.get("message")).isEqualTo("log message / java.lang.IllegalArgumentException / illegal argument exception message");
        assertThat(json.get("stacktrace")).isNotNull();
        assertThat(json.get("stacktrace").contains("java.lang.NullPointerException")).isTrue();
        assertThat(json.get("stacktrace").contains("null pointer exception message")).isTrue();
        assertThat(json.get("stacktrace").contains("java.lang.IllegalArgumentException")).isTrue();
        assertThat(json.get("stacktrace").contains("illegal argument exception message")).isTrue();
    }

    @Test
    @DisplayName("should ignore the MDC entries that use reserved keys")
    void reservedKeys() {
        MDC.put("message", "foo");
        MDC.put("stacktrace", "bar");
        LOGGER_CONTEXT.getLogger("TEST_LOGGER").info("log message");
        var json = getLogOutput();
        assertThat(json.get("message")).isEqualTo("log message");
        assertThat(json.get("stacktrace")).isNull();
    }

    @SneakyThrows
    private Map<String, String> getLogOutput() {
        var output = LOG_OUTPUT.toString();
        return new ObjectMapper().readValue(output, new TypeReference<>() {
        });
    }

    private static final class ResettableStringWriter extends Writer {

        private StringWriter delegate;

        public ResettableStringWriter() {
            delegate = new StringWriter();
        }

        synchronized void reset() throws Exception {
            delegate.close();
            delegate = new StringWriter();
        }

        @Override
        public void write(char[] cbuf, int off, int len) {
            delegate.write(cbuf, off, len);
        }

        @Override
        public void flush() {
            delegate.flush();
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }

        @Override
        public String toString() {
            flush();
            return delegate.toString();
        }

    }

}
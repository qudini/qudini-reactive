package com.qudini.reactive.logging.log4j2;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import lombok.SneakyThrows;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;
import java.util.StringJoiner;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.ofEpochMilli;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.apache.logging.log4j.core.Layout.ELEMENT_TYPE;
import static org.apache.logging.log4j.core.config.Node.CATEGORY;

@Plugin(name = "QudiniJsonLayout", category = CATEGORY, elementType = ELEMENT_TYPE)
public final class QudiniJsonLayout extends AbstractStringLayout {

    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String LEVEL_KEY = "level";
    private static final String THREAD_KEY = "thread";
    private static final String LOGGER_KEY = "logger";
    private static final String MESSAGE_KEY = "message";
    private static final String STACKTRACE_KEY = "stacktrace";

    private static final Set<String> RESERVED_KEYS = Set.of(
            TIMESTAMP_KEY,
            LEVEL_KEY,
            THREAD_KEY,
            LOGGER_KEY,
            MESSAGE_KEY,
            STACKTRACE_KEY
    );

    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    private QudiniJsonLayout() {
        super(UTF_8);
    }

    @PluginFactory
    public static QudiniJsonLayout newInstance() {
        return new QudiniJsonLayout();
    }

    @SneakyThrows
    public String toSerializable(LogEvent logEvent) {

        var timestamp = logEvent.getTimeMillis();
        var thread = logEvent.getThreadName();
        var loggerName = logEvent.getLoggerName();
        var logLevel = logEvent.getLevel().toString();

        var message = new StringJoiner(" / ");
        var logMessage = logEvent.getMessage().getFormattedMessage();
        if (null != logMessage) {
            message.add(logMessage);
        }

        final String stacktrace;
        var thrown = logEvent.getThrown();
        if (null != thrown) {
            message.add(thrown.getClass().getName());
            var exceptionMessage = thrown.getMessage();
            if (null != exceptionMessage) {
                message.add(exceptionMessage);
            }
            stacktrace = readStackTrace(thrown);
        } else {
            stacktrace = null;
        }

        var mdc = logEvent.getContextData();

        try (
                var writer = new StringWriter();
                var generator = JSON_FACTORY.createGenerator(writer)
        ) {
            generator.writeStartObject();
            mdc.forEach((key, value) -> writeMdcEntry(generator, key, value));
            generator.writeStringField(TIMESTAMP_KEY, ISO_INSTANT.format(ofEpochMilli(timestamp)));
            generator.writeStringField(LEVEL_KEY, logLevel);
            generator.writeStringField(THREAD_KEY, thread);
            generator.writeStringField(LOGGER_KEY, loggerName);
            generator.writeStringField(MESSAGE_KEY, message.toString());
            if (null != stacktrace) {
                generator.writeStringField(STACKTRACE_KEY, stacktrace);
            }
            generator.writeEndObject();
            generator.flush();
            return writer + "\n";
        }

    }

    @SneakyThrows
    private String readStackTrace(Throwable throwable) {
        try (
                var stringWriter = new StringWriter();
                var printWriter = new PrintWriter(stringWriter)
        ) {
            throwable.printStackTrace(printWriter);
            printWriter.flush();
            return stringWriter.toString();
        }
    }

    @SneakyThrows
    private void writeMdcEntry(JsonGenerator generator, String key, Object value) {
        if (!RESERVED_KEYS.contains(key)) {
            generator.writeStringField(key, String.valueOf(value));
        }
    }

}

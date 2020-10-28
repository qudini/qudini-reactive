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

import static java.nio.charset.StandardCharsets.UTF_8;
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

    public String toSerializable(LogEvent event) {
        return toSerializable(QudiniLogEvent.of(event));
    }

    @SneakyThrows
    private String toSerializable(QudiniLogEvent event) {
        try (
                var writer = new StringWriter();
                var generator = JSON_FACTORY.createGenerator(writer)
        ) {
            generator.writeStartObject();
            event.getContext().forEach((key, value) -> writeMdcEntry(generator, key, value));
            writeEntry(generator, TIMESTAMP_KEY, ISO_INSTANT.format(event.getTimestamp()));
            writeEntry(generator, LEVEL_KEY, event.getLevel().name());
            writeEntry(generator, MESSAGE_KEY, event.getMessage());
            event.getThread().ifPresent(thread -> writeEntry(generator, THREAD_KEY, thread));
            event.getLogger().ifPresent(logger -> writeEntry(generator, LOGGER_KEY, logger));
            event.getError().map(this::readStackTrace).ifPresent(stacktrace -> writeEntry(generator, STACKTRACE_KEY, stacktrace));
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

    private void writeMdcEntry(JsonGenerator generator, String key, Object value) {
        if (!RESERVED_KEYS.contains(key)) {
            writeEntry(generator, key, String.valueOf(value));
        }
    }

    @SneakyThrows
    private void writeEntry(JsonGenerator generator, String key, String value) {
        generator.writeStringField(key, value);
    }

}

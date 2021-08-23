package com.qudini.reactive.logging.log4j2;

import com.qudini.reactive.utils.metadata.MetadataService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;

public final class QudiniLogEvent {

    private static final AtomicBoolean INITIALISED = new AtomicBoolean(false);

    private static Optional<String> environment = Optional.empty();
    private static Optional<String> buildName = Optional.empty();
    private static Optional<String> buildVersion = Optional.empty();

    private final LogEvent originalEvent;
    private final Instant timestamp;
    private final Optional<String> thread;
    private final Optional<String> logger;
    private final Level level;
    private final Optional<Throwable> error;
    private final String message;
    private final Map<String, String> context;

    private QudiniLogEvent(LogEvent event) {

        this.originalEvent = event;

        this.timestamp = Instant.ofEpochMilli(event.getTimeMillis());
        this.thread = Optional.ofNullable(event.getThreadName());
        this.logger = Optional.ofNullable(event.getLoggerName());
        this.level = event.getLevel();
        this.error = Optional.ofNullable(event.getThrown());
        this.context = Optional.ofNullable(event.getContextData()).map(ReadOnlyStringMap::toMap).map(Map::copyOf).orElseGet(Map::of);

        var message = new StringJoiner(" / ");
        Optional.ofNullable(event.getMessage()).map(Message::getFormattedMessage).ifPresent(message::add);
        error.ifPresent(error -> addErrorToMessage(message, error));

        this.message = message.toString();

    }

    private void addErrorToMessage(StringJoiner message, Throwable error) {
        message.add(error.getClass().getName());
        var exceptionMessage = error.getMessage();
        if (null != exceptionMessage) {
            message.add(exceptionMessage);
        }
    }

    public LogEvent getOriginalEvent() {
        return originalEvent;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Optional<String> getThread() {
        return thread;
    }

    public Optional<String> getLogger() {
        return logger;
    }

    public Level getLevel() {
        return level;
    }

    public Optional<Throwable> getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public Optional<String> getEnvironment() {
        return environment;
    }

    public Optional<String> getBuildName() {
        return buildName;
    }

    public Optional<String> getBuildVersion() {
        return buildVersion;
    }

    public static QudiniLogEvent of(LogEvent event) {
        return new QudiniLogEvent(event);
    }

    public static void init(MetadataService metadataService) {
        if (INITIALISED.compareAndSet(false, true)) {
            environment = Optional.of(metadataService.getEnvironment());
            buildName = Optional.of(metadataService.getBuildName());
            buildVersion = Optional.of(metadataService.getBuildVersion());
        }
    }

    public static void reset() {
        if (INITIALISED.compareAndSet(true, false)) {
            environment = Optional.empty();
            buildName = Optional.empty();
            buildVersion = Optional.empty();
        }
    }

}

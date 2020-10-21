package com.qudini.reactive.logging.log4j2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import java.time.Instant;
import java.util.Optional;
import java.util.StringJoiner;

public final class QudiniLogEvent {

    private final LogEvent originalEvent;
    private final Instant timestamp;
    private final Optional<String> thread;
    private final Optional<String> logger;
    private final Level level;
    private final Optional<Throwable> error;
    private final String message;
    private final ReadOnlyStringMap context;

    private QudiniLogEvent(LogEvent event) {

        this.originalEvent = event;
        this.timestamp = Instant.ofEpochMilli(event.getTimeMillis());
        this.thread = Optional.ofNullable(event.getThreadName());
        this.logger = Optional.ofNullable(event.getLoggerName());
        this.level = event.getLevel();
        this.error = Optional.ofNullable(event.getThrown());
        this.context = Optional.ofNullable(event.getContextData()).orElseGet(ContextDataFactory::emptyFrozenContextData);

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

    public ReadOnlyStringMap getContext() {
        return context;
    }

    static QudiniLogEvent of(LogEvent event) {
        return new QudiniLogEvent(event);
    }

}

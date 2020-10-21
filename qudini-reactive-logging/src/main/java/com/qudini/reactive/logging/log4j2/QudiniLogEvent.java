package com.qudini.reactive.logging.log4j2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import java.time.Instant;
import java.util.StringJoiner;

public final class QudiniLogEvent {

    private final Instant timestamp;
    private final String thread;
    private final String logger;
    private final Level level;
    private final Throwable error;
    private final String message;
    private final ReadOnlyStringMap context;

    QudiniLogEvent(LogEvent event) {

        this.timestamp = Instant.ofEpochMilli(event.getTimeMillis());
        this.thread = event.getThreadName();
        this.logger = event.getLoggerName();
        this.level = event.getLevel();
        this.error = event.getThrown();
        this.context = event.getContextData();

        var message = new StringJoiner(" / ");
        var logMessage = event.getMessage().getFormattedMessage();
        if (null != logMessage) {
            message.add(logMessage);
        }

        if (null != error) {
            message.add(error.getClass().getName());
            var exceptionMessage = error.getMessage();
            if (null != exceptionMessage) {
                message.add(exceptionMessage);
            }
        }

        this.message = message.toString();

    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getThread() {
        return thread;
    }

    public String getLogger() {
        return logger;
    }

    public Level getLevel() {
        return level;
    }

    public Throwable getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public ReadOnlyStringMap getContext() {
        return context;
    }

}

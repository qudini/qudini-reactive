package com.qudini.reactive.logging.log4j2.trackers;

import com.qudini.reactive.logging.log4j2.QudiniLogEvent;
import com.qudini.reactive.logging.log4j2.Tracker;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Message;
import org.apache.logging.log4j.Level;

import java.sql.Date;
import java.util.Map;

public final class SentryTracker implements Tracker {

    static {
        Sentry.init(options -> options.setEnableExternalConfiguration(true));
    }

    @Override
    public void track(QudiniLogEvent event) {
        Sentry.captureEvent(toSentryEvent(event));
    }

    private static SentryEvent toSentryEvent(QudiniLogEvent logEvent) {
        var sentryEvent = new SentryEvent(Date.from(logEvent.getTimestamp()));
        sentryEvent.setMessage(toSentryMessage(logEvent.getMessage()));
        sentryEvent.setLevel(toSentryLevel(logEvent.getLevel()));
        sentryEvent.setContexts(toSentryContexts(logEvent));
        logEvent.getEnvironment().ifPresent(sentryEvent::setEnvironment);
        logEvent.getBuildVersion().ifPresent(sentryEvent::setRelease);
        logEvent.getLogger().ifPresent(sentryEvent::setLogger);
        logEvent.getError().ifPresent(sentryEvent::setThrowable);
        return sentryEvent;
    }

    private static Message toSentryMessage(String logMessage) {
        var message = new Message();
        message.setFormatted(logMessage);
        return message;
    }

    private static SentryLevel toSentryLevel(Level logLevel) {
        if (logLevel.isMoreSpecificThan(Level.FATAL)) {
            return SentryLevel.FATAL;
        } else if (logLevel.isMoreSpecificThan(Level.ERROR)) {
            return SentryLevel.ERROR;
        } else if (logLevel.isMoreSpecificThan(Level.WARN)) {
            return SentryLevel.WARNING;
        } else if (logLevel.isMoreSpecificThan(Level.INFO)) {
            return SentryLevel.INFO;
        } else {
            return SentryLevel.DEBUG;
        }
    }

    private static Contexts toSentryContexts(QudiniLogEvent logEvent) {
        var contexts = new Contexts();
        logEvent.getContext().forEach((key, value) -> addContext(contexts, key, value));
        return contexts;
    }

    private static void addContext(Contexts contexts, String key, String value) {
        contexts.put(key, Map.of("value", value));
    }

}

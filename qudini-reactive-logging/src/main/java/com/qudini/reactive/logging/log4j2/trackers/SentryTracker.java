package com.qudini.reactive.logging.log4j2.trackers;

import com.qudini.reactive.logging.log4j2.Tracker;
import io.sentry.Sentry;
import org.apache.logging.log4j.core.LogEvent;

import java.util.Optional;

public final class SentryTracker implements Tracker {

    @Override
    public void track(LogEvent event) {
        Sentry.configureScope(scope -> event.getContextData().forEach(scope::setContexts));
        Optional.ofNullable(event.getThrown()).ifPresentOrElse(
                Sentry::captureException,
                () -> Sentry.captureMessage(event.getMessage().getFormattedMessage())
        );
    }

}

package com.qudini.reactive.logging.log4j2.trackers;

import com.qudini.reactive.logging.log4j2.QudiniLogEvent;
import com.qudini.reactive.logging.log4j2.Tracker;
import io.sentry.Sentry;

import java.util.Optional;

public final class SentryTracker implements Tracker {

    @Override
    public void track(QudiniLogEvent event) {
        Sentry.configureScope(scope -> event.getContext().forEach(scope::setContexts));
        Optional.ofNullable(event.getError()).ifPresentOrElse(
                Sentry::captureException,
                () -> Sentry.captureMessage(event.getMessage())
        );
    }

}

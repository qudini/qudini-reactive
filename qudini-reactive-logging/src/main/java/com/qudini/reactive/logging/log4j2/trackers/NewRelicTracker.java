package com.qudini.reactive.logging.log4j2.trackers;

import com.newrelic.api.agent.NewRelic;
import com.qudini.reactive.logging.log4j2.QudiniLogEvent;
import com.qudini.reactive.logging.log4j2.Tracker;

public final class NewRelicTracker implements Tracker {

    @Override
    public void track(QudiniLogEvent event) {
        event.getError().ifPresentOrElse(
                error -> NewRelic.noticeError(error, event.getContext()),
                () -> NewRelic.noticeError(event.getMessage(), event.getContext())
        );
    }

}

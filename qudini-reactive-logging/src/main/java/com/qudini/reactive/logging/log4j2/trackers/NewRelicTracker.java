package com.qudini.reactive.logging.log4j2.trackers;

import com.newrelic.api.agent.NewRelic;
import com.qudini.reactive.logging.log4j2.QudiniLogEvent;
import com.qudini.reactive.logging.log4j2.Tracker;

import java.util.Optional;

public final class NewRelicTracker implements Tracker {

    @Override
    public void track(QudiniLogEvent event) {
        event.getContext().forEach((key, value) -> NewRelic.addCustomParameter(key, String.valueOf(value)));
        Optional.ofNullable(event.getError()).ifPresentOrElse(
                NewRelic::noticeError,
                () -> NewRelic.noticeError(event.getMessage())
        );
    }

}

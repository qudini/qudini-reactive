package com.qudini.reactive.logging.log4j2.trackers;

import com.newrelic.api.agent.NewRelic;
import com.qudini.reactive.logging.log4j2.Tracker;
import org.apache.logging.log4j.core.LogEvent;

import java.util.Optional;

public final class NewRelicTracker implements Tracker {

    @Override
    public void track(LogEvent event) {
        event.getContextData().forEach((key, value) -> NewRelic.addCustomParameter(key, String.valueOf(value)));
        Optional.ofNullable(event.getThrown()).ifPresentOrElse(
                NewRelic::noticeError,
                () -> NewRelic.noticeError(event.getMessage().getFormattedMessage())
        );
    }

}

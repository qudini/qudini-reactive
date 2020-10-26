package com.qudini.reactive.logging.log4j2.trackers;

import com.newrelic.api.agent.NewRelic;
import com.qudini.reactive.logging.log4j2.QudiniLogEvent;
import com.qudini.reactive.logging.log4j2.Tracker;

import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

public final class NewRelicTracker implements Tracker {

    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String LEVEL_KEY = "level";
    private static final String LOGGER_KEY = "logger";
    private static final String MESSAGE_KEY = "message";

    @Override
    public void track(QudiniLogEvent event) {
        event.getError().ifPresentOrElse(
                error -> NewRelic.noticeError(error, toNewRelicParams(event)),
                () -> NewRelic.noticeError(event.getMessage(), toNewRelicParams(event))
        );
    }

    private static Map<String, String> toNewRelicParams(QudiniLogEvent event) {
        var params = new HashMap<>(event.getContext());
        params.put(TIMESTAMP_KEY, ISO_INSTANT.format(event.getTimestamp()));
        params.put(LEVEL_KEY, event.getLevel().name());
        event.getLogger().ifPresent(logger -> params.put(LOGGER_KEY, logger));
        params.put(MESSAGE_KEY, event.getMessage());
        return params;
    }

}

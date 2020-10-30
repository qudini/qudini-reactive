package com.qudini.reactive.logging.log4j2.trackers;

import com.newrelic.api.agent.NewRelic;
import com.qudini.reactive.logging.log4j2.QudiniLogEvent;
import com.qudini.reactive.logging.log4j2.Tracker;

import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

public final class NewRelicTracker implements Tracker {

    private static final String BUILD_VERSION = "build_version";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String LEVEL_KEY = "level";
    private static final String LOGGER_KEY = "logger";
    private static final String MESSAGE_KEY = "message";

    @Override
    public void track(QudiniLogEvent event) {
        var params = toNewRelicParams(event);
        event.getError().ifPresentOrElse(
                error -> NewRelic.noticeError(error, params),
                () -> NewRelic.noticeError(event.getMessage(), params)
        );
    }

    private static Map<String, String> toNewRelicParams(QudiniLogEvent event) {
        var params = new HashMap<>(event.getContext());
        params.put(TIMESTAMP_KEY, ISO_INSTANT.format(event.getTimestamp()));
        params.put(LEVEL_KEY, event.getLevel().name());
        params.put(MESSAGE_KEY, event.getMessage());
        event.getLogger().ifPresent(logger -> params.put(LOGGER_KEY, logger));
        event.getBuildVersion().ifPresent(buildVersion -> params.put(BUILD_VERSION, buildVersion));
        return params;
    }

}

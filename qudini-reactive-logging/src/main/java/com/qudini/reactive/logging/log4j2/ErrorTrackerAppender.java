package com.qudini.reactive.logging.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.apache.logging.log4j.Level.ERROR;
import static org.apache.logging.log4j.core.Appender.ELEMENT_TYPE;
import static org.apache.logging.log4j.core.Core.CATEGORY_NAME;

@Plugin(name = "ErrorTrackerAppender", category = CATEGORY_NAME, elementType = ELEMENT_TYPE)
public final class ErrorTrackerAppender extends AbstractAppender {

    private final Set<Tracker> trackers;

    private ErrorTrackerAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
        this.trackers = loadTrackers();
    }

    @PluginFactory
    public static ErrorTrackerAppender newInstance(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        return new ErrorTrackerAppender(name, filter, layout, ignoreExceptions, properties);
    }

    @Override
    public void append(LogEvent logEvent) {
        if (logEvent.getLevel().isMoreSpecificThan(ERROR) && !trackers.isEmpty()) {
            var event = QudiniLogEvent.of(logEvent);
            trackers.forEach(tracker -> tracker.track(event));
        }
    }

    private static Set<Tracker> loadTrackers() {
        return Stream
                .of(
                        loadTracker("NewRelic", "com.newrelic.api.agent.NewRelic"),
                        loadTracker("Sentry", "io.sentry.Sentry")
                )
                .flatMap(Optional::stream)
                .collect(toUnmodifiableSet());
    }

    private static Optional<Tracker> loadTracker(String trackerName, String conditionalClassName) {
        if (classExists(conditionalClassName)) {
            var trackerClassName = "com.qudini.reactive.logging.log4j2.trackers." + trackerName + "Tracker";
            try {
                var tracker = (Tracker) Class.forName(trackerClassName).getDeclaredConstructor().newInstance();
                return Optional.of(tracker);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to load tracker " + trackerClassName, e);
            }
        } else {
            return Optional.empty();
        }
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}

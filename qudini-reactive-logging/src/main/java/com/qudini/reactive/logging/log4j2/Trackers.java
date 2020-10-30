package com.qudini.reactive.logging.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.apache.logging.log4j.core.Appender.ELEMENT_TYPE;
import static org.apache.logging.log4j.core.Core.CATEGORY_NAME;

@Plugin(name = "Trackers", category = CATEGORY_NAME, elementType = ELEMENT_TYPE)
public final class Trackers extends AbstractAppender {

    private static final Set<Tracker> TRACKERS = loadTrackers();

    private Trackers(String name, Filter filter) {
        super(name, filter, null, true, Property.EMPTY_ARRAY);
    }

    @PluginFactory
    public static Trackers newInstance(@PluginAttribute("name") String name, @PluginElement("Filter") Filter filter) {
        return new Trackers(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        append(QudiniLogEvent.of(event));
    }

    private void append(QudiniLogEvent event) {
        TRACKERS.forEach(tracker -> tracker.track(event));
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
                System.err.println(e.getLocalizedMessage());
                return Optional.empty();
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
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return false;
        }
    }

}

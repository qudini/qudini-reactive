package com.qudini.reactive.logging.log4j2;

import org.apache.logging.log4j.core.LogEvent;

public interface Tracker {

    void track(LogEvent event);

}

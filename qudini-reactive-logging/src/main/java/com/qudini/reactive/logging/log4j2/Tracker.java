package com.qudini.reactive.logging.log4j2;

import com.qudini.reactive.utils.metadata.MetadataService;

public interface Tracker {

    default void init(MetadataService metadataService) {
        // no-op by default
    }

    void track(QudiniLogEvent event);

}
